public enum Opinion {
	FOR(1), AGAINST(-1);

	public final int opinionValue;

	Opinion(int val) {
		this.opinionValue = val;
	}

}