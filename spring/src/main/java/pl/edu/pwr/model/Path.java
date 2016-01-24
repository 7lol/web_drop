package pl.edu.pwr.model;

public class Path {

	private long id;
	
	private String name;
	
	private String path;

	private boolean started;

	private int threads;

	public Path(){
		id=0;
	}
	
	public Path(int id, String name, String path,boolean started, int threads){
		this.id = id;
		this.name = name;
		this.path = path;
		this.threads=threads;
		this.started=started;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public String getPath() {return path;}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Path))
			return false;
		Path other = (Path) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Path [id=" + id + ", name=" + name + ", path=" + path + "]";
	}
	

	
}
