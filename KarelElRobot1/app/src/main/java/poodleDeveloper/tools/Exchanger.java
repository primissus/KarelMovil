package poodleDeveloper.tools;

import java.io.File;

import android.os.Environment;
import poodleDeveloper.karel.data.karelmovil.KRunner;
import poodleDeveloper.karel.data.karelmovil.KWorld;

public class Exchanger {

	public static final String PARENT_ROOT_PATH = Environment.getExternalStorageDirectory().toString();
	public static final String ROOT_PATH = Environment.getExternalStorageDirectory()+File.separator+"KarelTheRobot";
	public static final String WORLD_PATH = ROOT_PATH+File.separator+"Mundos";
	public static final String CODE_PATH = ROOT_PATH+File.separator+"Códigos";
	public static final String KARELAPAN_PATH = ROOT_PATH+File.separator+"Karelapan";
	public static KWorld kworld;
	public static KRunner krunner;
	public static File code;
	public static File world;
	public static boolean SUCESS_EXECUTED;
	public static boolean KARELAPAN_MODE_ACTIVATED;
}
