package com.infa.products.discovery.automatedclassification.util;

/**
 * @author halshi
 */
public class DiscoveryConstants {

    public static final String PATH = "/access";

    /*
     * Miscellaneous constants
     */

    public static final String COLON = ":";

    public static final String UNDERSCORE = "_";

    public static final String SPACE = " ";

    public static final String XDOC_PREFIX = "xdoc_";

    public static final String USER = "userName";

    public static final String PASSWORD = "password";

    public static final String HOST = "host";

    public static final String PORT = "port";

    public static final String NAMESPACE = "namespace";

    public static final String SSL_ENABLED = "issslenabled";

    public static final String USE_SYNONYMS_AND_STOP_WORDS = "isUseSynonymsAndStopWords";

    public static final String PREFIXES_TO_IGNORE = "prefixes";

    public static final String ID = "id";

    public static final String COLUMN_FACTS = "facts";

    public static final String LINK_PROPERTIES = "linkProperties";

    public static final String COLUMN_FACT_NAME = "name";

    public static final String COLUMN_FACT_VALUE = "value";

    public static final String PROJECTED_FROM_HACK = "projectedFrom"; // This is a hack provided to eliminate a bug.

    public static final String DOMAIN_ID = "value";

    public static final String SIM_COLUMN_FACT_NAME = "name";

    public static final String SIM_COLUMN_FACT_VALUE = "value";

    public static final String SOURCE_SIMILAR_COLUMNS = "srcObjects";

    public static final String DESTINATION_SIMILAR_COLUMNS = "dstObjects";

    public static final String CREATED_BY = "createdBy";

    public static final String RESOURCE_NAME = "resourceName";

    public static final String SYSTEM_CREATED_RESOURCE = "system";

    public static final String CORE_RESOURCE_NAME = "core.resourceName";

    public static final String RESOURCE_ID = "resource_id";

    public static final String RESOURCE = "Resource";

    public static final String CORE_DATASET_UUID = "core.dataSetUuid";

    public static final String TERM_ASSOCIATION_RESULTS_HEADER = "Asset,BusinessTerm,FeatureType,Score,GlossaryType,AssetType";

    public static final String INFERENCE_PROVIDER_VALUE = "SEQUENCE_ALIGNMENT";

    public static final String COMMA_SEPARATOR = ",";

    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static final String CORE_NAME = "core.name";

    public static final String CORE_CLASSTYPE = "core.classType";

    public static final String BG_SYNONYM_ATTRIBUTE_NAME = "com.infa.ldm.bg.synonyms";

    public static final String AXON_SYNONYM_ATTRIBUTE_NAME = "com.infa.ldm.axon.aliasNames";

    public static final String FORWARD_SLASH = "/";

    public static final String BACKWARD_SLASH = "\\";

    public static final String AXON_GLOSSARY_CLASS_TYPE = "com.infa.ldm.axon.AxonGlossary";

    public static final String BUSINESS_GLOSSARY_CLASS_TYPE = "com.infa.ldm.bg.BGTerm";

    public static final String AXON = "AXON";

    public static final String BG = "BG";

    public static final String OPENING_BRACES = "(";

    public static final String CLOSING_BRACES = ")";

    public static final String OR = "|";

    public static final String DEFAULT_SEARCH_THREADS_COUNT = "10";

    public static final String SEARCH_THREADS_COUNT_OPTION = "search.thread.count";

    public static final String TERM_ASSOCIATION_SCANNER_CHECKPOINT_KEY = "term.association.assets.processed";

    public static final String EMPTY_STRING = "";

    public static final String DEFAULT_ASSETS_TO_PROCESS_BATCH_SIZE = "1000";

    public static final String BATCH_SIZE_OPTION = "assets.batch.size";

    public static final String DOT = ".";

    public static final String TERM_ASSOCIATION_EXECUTOR = "TermAssociationExecutor";

    public static final String TERM_ASSOCIATION_SCANNER_ID = "TermAssociationScanner";

    public static final String DEFAULT_CUT_OFF_SCORE = "60";

    public static final String CUT_OFF_SCORE_OPTION = "cut.off.score";

    public static final String DEFAULT_MAX_CANDIDATES_CONSIDERED = "500";

    public static final String MAX_CANDIDATES_CONSIDERED_OPTION = "max.candidates.considered";

    public static final String DEFAULT_MAX_RESULTS_ACCEPTED = "10";

    public static final String MAX_RESULTS_ACCEPTED_OPTION = "max.results.accepted";

    public static final String DEFAULT_MAX_DETERMINIZED_STATES = "10000";

    public static final String MAX_DETERMINIZED_STATES_OPTION = "max.determinized.states";

    public static final String DEFAULT_CHAR_MATCH_SCORE = "2";

    public static final String CHAR_MATCH_SCORE_OPTION = "char.match.score";

    public static final String DEFAULT_STOP_WORD_MATCH_BOOST = "2";

    public static final String STOP_WORD_MATCH_BOOST_OPTION = "stop.word.match.boost";

    public static final String DEFAULT_WORD_MATCH_BOOST = "4";

    public static final String WORD_MATCH_BOOST_OPTION = "word.match.boost";

    public static final String TERM_ASSOCIATION_SCORE_COMPUTATION_TASK = "Term Association Score Computation Task";

    public static final String TERM_INDEXING_TASK = "Term Indexing Task";

    public static final String TERM_ASSOCIATION_SCANNER_STAGE_PERSIST_PATH = "/propagation";

    public static final String CORE_DATASET = "core.DataSet";

    public static final String CORE_DATAELEMENT = "core.DataElement";

    public static final String CORE_ALLCLASSTYPES = "core.allclassTypes";

    public static final String CORE_RESOURCETYPE = "core.resourceType";

    public static final String GOOGLE_BIG_QUERY_RESOURCE_TYPE = "Google BigQuery";

    public static final String DOT_SEPARATOR = "\\.";

}