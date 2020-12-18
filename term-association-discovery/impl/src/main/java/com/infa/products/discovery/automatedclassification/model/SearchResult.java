package com.infa.products.discovery.automatedclassification.model;

public final class SearchResult<D> {

    private final Query query;

    private final D document;
    
    private final String matchedString;

	private final float similarityScore;

	private SearchResult(final Builder<D> builder) {
		this.query = builder.query;
		this.document = builder.document;
		this.matchedString = builder.matchedString;
		this.similarityScore = builder.similarityScore;
	}

    public Query getQuery() {
        return query;
    }

    public D getDocument() {
        return document;
    }

    public float getSimilarityScore() {
        return similarityScore;
    }
    
    public String getMatchedString() {
		return matchedString;
	}

	/**
	 * Creates builder to build {@link SearchResult}.
	 * @return created builder
	 */
	public static <D> Builder<D> builder() {
		return new Builder<D>();
	}

	/**
	 * Builder to build {@link SearchResult}.
	 */
	public static final class Builder<D> {
		private Query query;
		private D document;
		private String matchedString;
		private float similarityScore;

		private Builder() {
		}

		public Builder<D> withQuery(Query query) {
			this.query = query;
			return this;
		}

		public Builder<D> withDocument(D document) {
			this.document = document;
			return this;
		}

		public Builder<D> withMatchedString(String matchedString) {
			this.matchedString = matchedString;
			return this;
		}

		public Builder<D> withSimilarityScore(float similarityScore) {
			this.similarityScore = similarityScore;
			return this;
		}

		public SearchResult<D> build() {
			return new SearchResult<D>(this);
		}
	}

}
