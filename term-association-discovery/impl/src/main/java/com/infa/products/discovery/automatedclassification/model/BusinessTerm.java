package com.infa.products.discovery.automatedclassification.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class BusinessTerm implements Document {

    private final String catalogId;

    private final String name;
    
    private final Set<String> synonyms;

    private final TermType type;

	private BusinessTerm(final Builder builder) {
		this.catalogId = builder.catalogId;
		this.name = builder.name;
		this.synonyms = Objects.isNull(builder.synonyms) ? Collections.emptySet() : builder.synonyms;
		this.type = builder.type;
	}

    public String getCatalogId() {
        return catalogId;
    }

    public String getName() {
        return name;
    }
    
	public Set<String> getSynonyms() {
		return Objects.isNull(synonyms) ? Collections.emptySet() : Collections.unmodifiableSet(synonyms);
	}
    
    public TermType getType() {
        return type;
    }

	@Override
	public int hashCode() {
		return Objects.hash(catalogId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BusinessTerm other = (BusinessTerm) obj;
		return Objects.equals(catalogId, other.catalogId);
	}

	@Override
	public String toString() {
		return String.format("BusinessTerm { catalogId=%s, name=%s, synonym=%s, type=%s}", catalogId, name, synonyms,
				type);
	}

	/**
	 * Creates builder to build {@link BusinessTerm}.
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link BusinessTerm}.
	 */
	public static final class Builder {
		private String catalogId;
		private String name;
		private Set<String> synonyms;
		private TermType type;

		private Builder() {
		}

		public Builder withCatalogId(final String catalogId) {
			this.catalogId = catalogId;
			return this;
		}

		public Builder withName(final String name) {
			this.name = name;
			return this;
		}

		public Builder withSynonyms(final Set<String> synonyms) {
			this.synonyms = synonyms;
			return this;
		}

		public Builder withType(final TermType type) {
			this.type = type;
			return this;
		}

		public BusinessTerm build() {
			validate(this);
			return new BusinessTerm(this);
		}

		private void validate(final Builder builder) {
			Objects.requireNonNull(catalogId);
			Objects.requireNonNull(name);
			Objects.requireNonNull(type);
		}
	}

	@Override
	public String getId() {
		return catalogId;
	}
}
