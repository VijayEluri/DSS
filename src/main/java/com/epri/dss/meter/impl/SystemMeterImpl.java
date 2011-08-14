package com.epri.dss.meter.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.lang.mutable.MutableDouble;

import com.epri.dss.shared.impl.Complex;

import com.epri.dss.common.Circuit;
import com.epri.dss.common.SolutionObj;
import com.epri.dss.common.impl.DSSGlobals;
import com.epri.dss.common.impl.Utilities;
import com.epri.dss.meter.EnergyMeter;
import com.epri.dss.meter.SystemMeter;

public class SystemMeterImpl implements SystemMeter {

	private static double[] RegisterArray = new double[EnergyMeter.NUM_EM_REGISTERS];

	private MutableDouble kWh = new MutableDouble();
	private MutableDouble dkWh = new MutableDouble();
	private MutableDouble kvarh = new MutableDouble();
	private MutableDouble dkvarh = new MutableDouble();
	private MutableDouble Losseskwh = new MutableDouble();
	private MutableDouble dLosseskWh = new MutableDouble();
	private MutableDouble Losseskvarh = new MutableDouble();
	private MutableDouble dlosseskvarh = new MutableDouble();

	private double peakkW, peakkVA, PeakLosseskW;
	private boolean FirstSampleAfterReset, This_Meter_DIFileIsOpen;
	private FileWriter SystemDIFile;
	private Complex cPower, cLosses;

	public SystemMeterImpl() {
		clear();
		this.This_Meter_DIFileIsOpen = false;
	}

	/**
	 * Only called if "SaveDemandInterval".
	 */
	// FIXME Protected method in OpenDSS
	public void appendDemandIntervalFile() {
		String FileNm = "";

		DSSGlobals Globals = DSSGlobals.getInstance();

		if (This_Meter_DIFileIsOpen) return;

		try {
			FileNm = Globals.getEnergyMeterClass().getDI_Dir() + "/DI_SystemMeter.csv";
			/* File must exist */
			if (new File(FileNm).exists()) {
				SystemDIFile = new FileWriter(FileNm, true);
			} else {
				SystemDIFile = new FileWriter(FileNm, false);;
			}

			This_Meter_DIFileIsOpen = true;

		} catch (IOException e) {
			Globals.doSimpleMsg("Error opening demand interval file \""+FileNm+" for appending."+DSSGlobals.CRLF+e.getMessage(), 540);
		}
	}

	private void clear() {
		kWh.setValue(0.0);
		kvarh.setValue(0.0);
		peakkW = 0.0;
		peakkVA = 0.0;
		Losseskwh.setValue(0.0);
		Losseskvarh.setValue(0.0);
		PeakLosseskW = 0.0;
		dkWh.setValue(0.0);
		dkvarh.setValue(0.0);
		dLosseskWh.setValue(0.0);
		dlosseskvarh.setValue(0.0);
		FirstSampleAfterReset = true;
	}

	// FIXME Protected method in OpenDSS
	public void closeDemandIntervalFile() {
		if (This_Meter_DIFileIsOpen) {
			try {
				SystemDIFile.close();
				This_Meter_DIFileIsOpen = false;
			} catch (IOException e) {
				DSSGlobals.getInstance().doSimpleMsg("Error closing demand interval file."+DSSGlobals.CRLF+e.getMessage(), 540);
			}
		}
	}

	private void integrate(MutableDouble Reg, double Value, MutableDouble Deriv) {
		Circuit ckt = DSSGlobals.getInstance().getActiveCircuit();

		if (ckt.isTrapezoidalIntegration()) {
			/* Trapezoidal rule integration */
			if (!FirstSampleAfterReset)
				Reg.add(0.5 * ckt.getSolution().getIntervalHrs() * (Value + Deriv.doubleValue()));
		} else {
			/* Plain Euler integration */
			Reg.add(ckt.getSolution().getIntervalHrs() * Value);
		}

		Deriv.setValue(Value);
	}

	// FIXME Protected method in OpenDSS
	public void openDemandIntervalFile() {
		DSSGlobals Globals = DSSGlobals.getInstance();

		try {
			if (This_Meter_DIFileIsOpen)
				SystemDIFile.close();

			SystemDIFile = new FileWriter(Globals.getEnergyMeterClass().getDI_Dir()+"/DI_SystemMeter.csv");
			PrintWriter SystemDIPrinter = new PrintWriter(SystemDIFile);

			This_Meter_DIFileIsOpen = true;
			SystemDIPrinter.print("\"Hour\", ");
			writeRegisterNames(SystemDIPrinter);
			SystemDIPrinter.println();
		} catch (Exception e) {
			Globals.doSimpleMsg("Error opening demand interval file \"DI_SystemMeter.csv\"  for writing."+DSSGlobals.CRLF+e.getMessage(), 541);
		}
	}

	public void reset() {
		clear();
	}

	public void save() {
		String CSVName = "SystemMeter.csv", Folder;

		DSSGlobals Globals = DSSGlobals.getInstance();

		try {
			/* If we are doing a simulation and saving interval data, create this in the
			 * same directory as the demand interval data.
			 */
			if (Globals.getEnergyMeterClass().isSaveDemandInterval()) {
				Folder = Globals.getEnergyMeterClass().getDI_Dir() + "/";
			} else {
				Folder = Globals.getDSSDataDirectory();
			}
			FileWriter FStream = new FileWriter(Folder + CSVName, false);
			PrintWriter FPrinter = new PrintWriter(FStream);

			Globals.setGlobalResult(CSVName);

			FPrinter.write("Year, ");
			writeRegisterNames(FPrinter);
			FPrinter.println();

			FPrinter.print(Globals.getActiveCircuit().getSolution().getYear());
			writeRegisters(FPrinter);
			FPrinter.println();

			FPrinter.close();
			FStream.close();
		} catch (Exception e) {
			Globals.doSimpleMsg("Error opening system meter file \"" + DSSGlobals.CRLF + CSVName + "\": " + e.getMessage(), 542);
		}
	}

	/**
	 * Get total system energy out of the sources.
	 */
	public void takeSample() {

		cPower = Utilities.getTotalPowerFromSources().multiply(0.001);  // convert to kW

		integrate(kWh, cPower.getReal(), dkWh);
		integrate(kvarh, cPower.getImaginary(), dkvarh);

		peakkW  = Math.max(cPower.getReal(), peakkW);
		peakkVA = Math.max(cPower.abs(), peakkVA);

		/* Get total circuit losses */
		cLosses = DSSGlobals.getInstance().getActiveCircuit().getLosses();  // PD elements except shunts
		cLosses = cLosses.multiply(0.001);  // convert to kW

		integrate(Losseskwh, cLosses.getReal(), dLosseskWh);
		integrate(Losseskvarh, cLosses.getImaginary(), dlosseskvarh);

		PeakLosseskW = Math.max(cLosses.getReal(), PeakLosseskW);

		FirstSampleAfterReset = false;
		if (This_Meter_DIFileIsOpen) writeDemandIntervalData();
	}

	protected void writeDemandIntervalData() {
		SolutionObj sol = DSSGlobals.getInstance().getActiveCircuit().getSolution();

		PrintWriter SystemDIPrinter = new PrintWriter(SystemDIFile);

		SystemDIPrinter.printf("%-.6g", sol.getDblHour());
		SystemDIPrinter.printf(", %-g", cPower.getReal());
		SystemDIPrinter.printf(", %-g", cPower.getImaginary());
		SystemDIPrinter.printf(", %-g", peakkW);
		SystemDIPrinter.printf(", %-g", peakkVA);

		SystemDIPrinter.printf(", %-g", cLosses.getReal());
		SystemDIPrinter.printf(", %-g", cLosses.getImaginary());
		SystemDIPrinter.printf(", %-g", PeakLosseskW);
		SystemDIPrinter.println();

		SystemDIPrinter.close();
	}

	private void writeRegisterNames(PrintWriter F) {
		F.print("kWh, kvarh, \"Peak kW\", \"peak kVA\", \"Losses kWh\", \"Losses kvarh\", \"Peak Losses kW\"");
	}

	private void writeRegisters(PrintWriter F) {

		F.printf(", %-g", kWh.doubleValue());
		F.printf(", %-g", kvarh.doubleValue());
		F.printf(", %-g", peakkW);
		F.printf(", %-g", peakkVA);
		F.printf(", %-g", Losseskwh.doubleValue());
		F.printf(", %-g", Losseskvarh.doubleValue());
		F.printf(", %-g", PeakLosseskW);
	}

}
