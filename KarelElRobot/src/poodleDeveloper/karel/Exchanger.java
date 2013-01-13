package poodleDeveloper.karel;

import java.io.File;

import android.os.Environment;
import poodleDeveloper.karel.data.karelmovil.KWorld;

public class Exchanger {

	public static final String ROOT_PATH = Environment.getExternalStorageDirectory()+File.separator+"Karel el robot";
	public static final String WORLD_PATH = ROOT_PATH+File.separator+"Mundos";
	public static final String CODE_PATH = ROOT_PATH+File.separator+"Códigos";
	public static KWorld kworld;
}
