package com.infa.products.discovery.automatedclassification.model;

import com.infa.products.discovery.automatedclassification.model.api.DocumentBuilder;
import com.infa.products.discovery.automatedclassification.model.api.DocumentReader;
import com.infa.products.discovery.automatedclassification.model.api.DocumentType;

import java.util.*;

public class BusinessTermType implements DocumentType<BusinessTerm> {

	public static final Field BUSINESS_TERM_NAME_FIELD = new Field("businesstermname", Field.Type.TITLE);
	
	public static final Field BUSINESS_TERM_SYNONYMS_FIELD = new Field("businesstermsynonym", Field.Type.TITLE);

	public static final Field BUSINESS_TERM_ID_FIELD = new Field("businesstermid", Field.Type.TEXT);
	
	public static final Field BUSINESS_TERM_TYPE_FIELD = new Field("businesstermtype", Field.Type.TEXT);

	private static final List<Field> FIELDS = Collections
			.unmodifiableList(Arrays.asList(new Field[] { BUSINESS_TERM_NAME_FIELD, BUSINESS_TERM_SYNONYMS_FIELD, BUSINESS_TERM_ID_FIELD, BUSINESS_TERM_TYPE_FIELD }));

	public BusinessTermType() {
		
	}
	
	@Override
	public List<Field> getFields() {
		return FIELDS;
	}

	@Override
	public DocumentReader<BusinessTerm> getDocumentReader() {
		return new DocumentReader<BusinessTerm>() {

			@Override
			public Object getFieldValue(final BusinessTerm doc, final String field) {
				if (field.equals(BUSINESS_TERM_ID_FIELD.getFieldName())) {
					return doc.getCatalogId();
				} else if (field.equals(BUSINESS_TERM_NAME_FIELD.getFieldName())) {
					return doc.getName();
				} else if (field.equals(BUSINESS_TERM_SYNONYMS_FIELD.getFieldName())) {
					return doc.getSynonyms();
				} else if (field.equals(BUSINESS_TERM_TYPE_FIELD.getFieldName())) {
					return doc.getType();
				}
				throw new IllegalArgumentException("Field [" + field + "] does not exist in a business term.");
			}
		};
	}

	@Override
	public DocumentBuilder<BusinessTerm> getDocumentBuilder() {
		return new DocumentBuilder<BusinessTerm>() {

			private final BusinessTerm.Builder termBuilder = BusinessTerm.builder();

			@Override
			public DocumentBuilder<BusinessTerm> withField(final String field, final Object value) {
				if (field.equals(BUSINESS_TERM_ID_FIELD.getFieldName())) {
					final String catalogId = getCatalogId(value);
					termBuilder.withCatalogId(catalogId);
					return this;
				} else if (field.equals(BUSINESS_TERM_NAME_FIELD.getFieldName())) {
					final String name = getName(value);
					termBuilder.withName(name);
					return this;
				} else if (field.equals(BUSINESS_TERM_SYNONYMS_FIELD.getFieldName())) {
					final Set<String> synonyms = getSynonyms(value);
					termBuilder.withSynonyms(synonyms);
					return this;
				} else if (field.equals(BUSINESS_TERM_TYPE_FIELD.getFieldName())) {
					final TermType termType = getTermType(value);
					termBuilder.withType(termType);
					return this;
				}
				throw new IllegalArgumentException("Field [" + field + "] does not exist in a business term.");
			}

			private Set<String> getSynonyms(final Object value) {
				return Collections.unmodifiableSet(new HashSet<String>(
						Arrays.asList(objectToStringArray(value))));
			}

			private TermType getTermType(final Object value) {
				return TermType.valueOf(getStringValueOf(objectToStringArray(value)[0]));
			}

			private String getName(final Object value) {
				return getStringValueOf(objectToStringArray(value)[0]);
			}

			private String getCatalogId(final Object value) {
				return getStringValueOf(objectToStringArray(value)[0]);
			}

			private String[] objectToStringArray(final Object value) {
				return Objects.isNull(value) ? new String[0] : (String[])value;
			}

			private String getStringValueOf(final Object value) {
				return String.valueOf(value);
			}

			@Override
			public BusinessTerm build() {
				return termBuilder.build();
			}
		};
	}
}
