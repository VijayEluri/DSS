package com.epri.dss.general;

public interface CNLineConstants extends CableConstants {

	int getKStrand(int i);

	void setKStrand(int i, int kStrand);

	double getDiaStrand(int i);

	void setDiaStrand(int i, int units, double diaStrand);

	double getGmrStrand(int i);

	void setGmrStrand(int i, int units, double gmrStrand);

	double getRStrand(int i);

	void setRStrand(int i, int units, double rStrand);

}
