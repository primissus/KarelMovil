package poodleDeveloper.tools;

import java.io.Serializable;

public class Problema implements Serializable{

	private static final long serialVersionUID = 1L;
	public String title;
	public String description;
	public int best_time;
	public int best_score;
	public int time_solved;
	public String url_world;
}
