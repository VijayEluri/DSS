/**
 * Copyright (C) 2008-2012 Electric Power Research Institute, Inc.
 * Copyright (C) 2009-2012 Richard Lincoln
 * All rights reserved.
 */
package com.ncond.dss.meter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.ncond.dss.shared.Complex;

import com.ncond.dss.common.Circuit;
import com.ncond.dss.common.DSS;
import com.ncond.dss.common.SolutionObj;
import com.ncond.dss.common.Util;

public class SystemMeter {

	private double[] kWh = new double[1];
	private double[] dkWh = new double[1];
	private double[] kVArh = new double[1];
	private double[] dkVArh = new double[1];
	private double[] losses_kWh = new double[1];
	private double[] dLosses_kWh = new double[1];
	private double[] losses_kVArh = new double[1];
	private double[] dlosses_kVArh = new double[1];

	private double peakKW, peakKVA, peakLossesKW;
	private boolean firstSampleAfterReset, thisMeterDIFileIsOpen;
	private FileWriter systemDIFile;
	private Complex cPower, cLosses;

	public SystemMeter() {
		clear();
		thisMeterDIFileIsOpen = false;
	}

	/**
	 * Only called if "SaveDemandInterval".
	 */
	protected void appendDemandIntervalFile() {
		String fileName = "";

		if (thisMeterDIFileIsOpen) return;

		try {
			fileName = DSS.energyMeterClass.getDI_Dir() + DSS.SEPARATOR + "DI_SystemMeter.csv";
			/* File must exist */
			if (new File(fileName).exists()) {
				systemDIFile = new FileWriter(fileName, true);
			} else {
				systemDIFile = new FileWriter(fileName, false);;
			}

			thisMeterDIFileIsOpen = true;

		} catch (IOException e) {
			DSS.doSimpleMsg("Error opening demand interval file \"" + fileName +
					" for appending." + DSS.CRLF + e.getMessage(), 540);
		}
	}

	private void clear() {
		kWh[0] = 0.0 ;
		kVArh[0] = 0.0;
		peakKW = 0.0;
		peakKVA = 0.0;
		losses_kWh[0] = 0.0;
		losses_kVArh[0] = 0.0;
		peakLossesKW = 0.0;
		dkWh[0] = 0.0;
		dkVArh[0] = 0.0;
		dLosses_kWh[0] = 0.0;
		dlosses_kVArh[0] = 0.0;
		firstSampleAfterReset = true;
	}

	protected void closeDemandIntervalFile() {
		if (thisMeterDIFileIsOpen) {
			try {
				systemDIFile.close();
				thisMeterDIFileIsOpen = false;
			} catch (IOException e) {
				DSS.doSimpleMsg("Error closing demand interval file." +
						DSS.CRLF + e.getMessage(), 540);
			}
		}
	}

	private void integrate(double[] reg, double value, double[] deriv) {
		Circuit ckt = DSS.activeCircuit;

		if (ckt.isTrapezoidalIntegration()) {
			/* Trapezoidal rule integration */
			if (!firstSampleAfterReset)
				reg[0] += 0.5 * ckt.getSolution().getIntervalHrs() * (value + deriv[0]);
		} else {
			/* Plain Euler integration */
			reg[0] += ckt.getSolution().getIntervalHrs() * value;
		}

		deriv[0] = value;
	}

	protected void openDemandIntervalFile() {
		try {
			if (thisMeterDIFileIsOpen) systemDIFile.close();

			systemDIFile = new FileWriter(DSS.energyMeterClass.getDI_Dir() +
					DSS.SEPARATOR + "DI_SystemMeter.csv");
			PrintWriter pw = new PrintWriter(systemDIFile);

			thisMeterDIFileIsOpen = true;
			pw.print("\"Hour\", ");
			writeRegisterNames(pw);
			pw.println();
		} catch (Exception e) {
			DSS.doSimpleMsg("Error opening demand interval file \"DI_SystemMeter.csv\" for writing." +
					DSS.CRLF + e.getMessage(), 541);
		}
	}

	public void reset() {
		clear();
	}

	public void save() {
		String folder, csvName = "SystemMeter.csv";
		PrintWriter pw;

		try {
			/* If we are doing a simulation and saving interval data, create this in the
			 * same directory as the demand interval data.
			 */
			if (DSS.energyMeterClass.isSaveDemandInterval()) {
				folder = DSS.energyMeterClass.getDI_Dir() + DSS.SEPARATOR;
			} else {
				folder = DSS.dataDirectory;
			}
			pw = new PrintWriter(folder + csvName);

			DSS.globalResult = csvName;

			pw.print("Year, ");
			writeRegisterNames(pw);
			pw.println();

			pw.print(DSS.activeCircuit.getSolution().getYear());
			writeRegisters(pw);
			pw.println();

			pw.close();
		} catch (Exception e) {
			DSS.doSimpleMsg("Error opening system meter file \"" + DSS.CRLF +
					csvName + "\": " + e.getMessage(), 542);
		}
	}

	/**
	 * Get total system energy out of the sources.
	 */
	public void takeSample() {
		cPower = Util.getTotalPowerFromSources().mult(0.001);  // convert to kW

		integrate(kWh, cPower.real(), dkWh);
		integrate(kVArh, cPower.imag(), dkVArh);

		peakKW  = Math.max(cPower.real(), peakKW);
		peakKVA = Math.max(cPower.abs(), peakKVA);

		/* Get total circuit losses */
		cLosses = DSS.activeCircuit.getLosses();  // PD elements except shunts
		cLosses = cLosses.mult(0.001);  // convert to kW

		integrate(losses_kWh, cLosses.real(), dLosses_kWh);
		integrate(losses_kVArh, cLosses.imag(), dlosses_kVArh);

		peakLossesKW = Math.max(cLosses.real(), peakLossesKW);

		firstSampleAfterReset = false;
		if (thisMeterDIFileIsOpen) writeDemandIntervalData();
	}

	protected void writeDemandIntervalData() {
		SolutionObj sol = DSS.activeCircuit.getSolution();

		PrintWriter pw = new PrintWriter(systemDIFile);

		pw.printf("%-.6g", sol.getDblHour());
		pw.printf(", %g", cPower.real());
		pw.printf(", %g", cPower.imag());
		pw.printf(", %g", peakKW);
		pw.printf(", %g", peakKVA);

		pw.printf(", %g", cLosses.real());
		pw.printf(", %g", cLosses.imag());
		pw.printf(", %g", peakLossesKW);
		pw.println();

		pw.close();
	}

	private void writeRegisterNames(PrintWriter pw) {
		pw.print("kWh, kvarh, \"Peak kW\", \"peak kVA\", \"Losses kWh\", \"Losses kvarh\", \"Peak Losses kW\"");
	}

	private void writeRegisters(PrintWriter f) {
		f.printf(", %g", kWh[0]);
		f.printf(", %g", kVArh[0]);
		f.printf(", %g", peakKW);
		f.printf(", %g", peakKVA);
		f.printf(", %g", losses_kWh[0]);
		f.printf(", %g", losses_kVArh[0]);
		f.printf(", %g", peakLossesKW);
	}

}
