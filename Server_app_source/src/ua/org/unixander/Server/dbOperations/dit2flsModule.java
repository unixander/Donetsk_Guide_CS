package ua.org.unixander.Server.dbOperations;

import im1.UnitIM1;
import im2.UnitIM2;
import im3.UnitIM3;

import java.util.ArrayList;


import defuzzifier.Defuzzification;
import ekm.TypeReducer;

import lio.IOLV;
import rules.DBRules;
import ua.org.unixander.logger.ConsoleLog;

import fuzzifier.Fuzzification;
import fuzzysum.FuzzySum;

/**
 * 
 * Module for working with dit2fls library
 * @author unixander
 *
 */
public class dit2flsModule {
	private ArrayList<Double[][]>[] LI, LO;
	private Double[][] R;
	private UnitIM1 im1 = new UnitIM1();
	private UnitIM2 im2 = new UnitIM2();
	private UnitIM3 im3 = new UnitIM3();
	private Fuzzification fuzz = new Fuzzification();
	private DBRules rs = new DBRules();
	private FuzzySum fsumm = new FuzzySum();
	private IOLV lio = new IOLV("LI");
	private String path = "", MODEL_PATH = "\\model\\", LI_FILE = "LI.xml",
			LO_FILE = "LO.xml", R_FILE = "Rules.xml";
	private ConsoleLog console = new ConsoleLog();

	public dit2flsModule(String path) {
		this.path = path;
		LI = this.loadLI(path + MODEL_PATH + LI_FILE);
		LO = this.loadLO(path + MODEL_PATH + LO_FILE);
		R = this.loadRules(path + MODEL_PATH + R_FILE);
	}

	/**
	 * Load Linguistic input variable from file path
	 * 
	 * @param path
	 *            - path to file with LI variables
	 * @return variables structure for library
	 */
	public ArrayList<Double[][]>[] loadLI(String path) {
		lio.changeMode("LI");
		try {
			LI = lio.ReadFromXml(path);
		} catch (Exception e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
			LI = null;
		}
		return LI;
	}

	/**
	 * Load Linguistic output variable from file path
	 * 
	 * @param path
	 *            - path to file with LO variables
	 * @return variables structure for library
	 */
	public ArrayList<Double[][]>[] loadLO(String path) {
		lio.changeMode("LO");
		try {
			LO = lio.ReadFromXml(path);
		} catch (Exception e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
			LO = null;
		}
		return LO;
	}

	/**
	 * Load Rules from file path
	 * 
	 * @param path
	 *            - path to file with rules
	 * @return rules structure for library
	 */
	public Double[][] loadRules(String path) {
		try {
			R = rs.ReadFromXml(path);
		} catch (Exception e) {
			console.Log(e.getMessage(), ConsoleLog.ERRORMSG);
			R = null;
		}
		return R;
	}

	/**
	 * Do all the calculations with the library
	 * 
	 * @return result of calculations
	 */
	public Double toCalculate(Double... IN) {
		if (LI == null || LO == null || R == null) {
			return null;
		}
		Double res = null;
		TypeReducer tr = new TypeReducer();
		Defuzzification defuzz = new Defuzzification();
		try {
			Double[][] M = fuzz.GetMatrixM(IN, LI);
			im1.CalcRateRules(M, R, IN.length);
			Double[][][] Tact = im1.outTact();
			Double[][] Ract = im1.outRact();
			ArrayList<Double[][]> F = im2.CalcActMF(Ract, Tact, LO);
			Double[][] Y = im3.CalcResultMF(F);
			Double[] TR = tr.GetTypeReduceFS(Y);
			res = defuzz.GetValue(TR);
		} catch (Exception ex) {
			console.Log(ex.getMessage(), ConsoleLog.ERRORMSG);
		}
		return res;
	}

	public Double toCalculate(double[] rL, double[] hL) {
		if (LI == null || LO == null || R == null) {
			return null;
		}
		Double[] IN = new Double[2];
		Double[][] Ys;
		Double[][][] temp=new Double[rL.length][][];
		Double res = null;
		TypeReducer tr = new TypeReducer();
		Defuzzification defuzz = new Defuzzification();
		try {
			for (int i = 0; i < rL.length; i++) {
				IN[0]=rL[i];
				IN[1]=hL[i];
				Double[][] M = fuzz.GetMatrixM(IN, LI);
				im1.CalcRateRules(M, R, IN.length);
				Double[][][] Tact = im1.outTact();
				Double[][] Ract = im1.outRact();
				ArrayList<Double[][]> F = im2.CalcActMF(Ract, Tact, LO);
				Double[][] Y = im3.CalcResultMF(F);
				temp[i]=Y;
			}
			Ys = fsumm.getFuzzySum(temp);
			Double[] TR = tr.GetTypeReduceFS(Ys);
			res = defuzz.GetValue(TR);
		} catch (Exception ex) {
			console.Log(ex.getMessage(), ConsoleLog.ERRORMSG);
		}
		return res;
	};

}
