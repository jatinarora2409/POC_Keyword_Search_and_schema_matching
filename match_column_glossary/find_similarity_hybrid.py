
from find_similarity_util import *
from SchemaMatching import get_column_dict
### Number of predictions:

## For Union
# prediction_score_of_words_tokens = 0.8
# prediction_score_of_qgram = 0.7
# prediction_score_of_tfidf = 0.7

## For Interesection
prediction_score_of_words_tokens = 0.5
prediction_score_of_qgram = 0.3
prediction_score_of_tfidf = 0.58

## For intersection
prediction_score_of_words_tokens_bg = 0.65
prediction_score_of_qgram_bg = 0.45
prediction_score_of_tfidf_bg = 0.70

qgram_qval = 3
tokenizer_string = "QgramTokenizer"
space_tokenizer = sm.WhitespaceTokenizer()
tokenizer_string = "whitespace_tokensizer"
delim_tok = sm.DelimiterTokenizer(delim_set=['_'])
qgram_qval = 3
q_gram_tokenizer = sm.QgramTokenizer(qval=qgram_qval)
tokenizer_string = "QgramTokenizer"
similarity_measure = sm.OverlapCoefficient()
measure = "OverlapCoefficient"
#"_zillow","_redfin",
#sources = ["_zillow","_redfin","_realtor","_remax"]
sources = ["_zillow"]

for source in sources:
    print("\n")
    print("Source: "+source)
    schema_file = 'Schema'+source+'.txt'
    business_glossary_file = 'BusinessGlossary'+source+'.xml'
    assignment_capture_file = 'AssignmentCapture'+source+'.csv'
    set_of_columns = read_schema(schema_file)
    set_of_business_glossary,bg_terms_id_dict = get_businesss_glossary(business_glossary_file)
    gold_dict = get_assignment_capture_gold(assignment_capture_file)

    corpse_list = []

    for column in set_of_columns:
            glossary_term = " "
            tokenized_column_return, tokenized_glossary_return = stemming_code_on_words(glossary_term, column, delim_tok, space_tokenizer)
            corpse_list.append(tokenized_column_return)

    for glossary_term in set_of_business_glossary:
            column = " "
            tokenized_column_return, tokenized_glossary_return = stemming_code_on_words(glossary_term, column, delim_tok,space_tokenizer)
            corpse_list.append(tokenized_glossary_return)




    tf_idf = sm.TfIdf(corpus_list=corpse_list)
    output_list = []
    output_list_qgram = []
    output_list_tfidf = []

    ##output_list_business_glossary_matching
    output_list_bg = []
    output_list_qgram_bg = []
    output_list_tfidf_bg = []

    #print(source[1:len(source)])
    corresponding_bgs_terms_dict = get_column_dict(source[1:len(source)])
    #print(corresponding_bgs_terms_dict)
    for column in set_of_columns:
        for glossary_term in set_of_business_glossary:
            tokenized_column_return ,tokenized_glossary_return = stemming_code_on_words(glossary_term, column, delim_tok, space_tokenizer)
            score = similarity_measure.get_sim_score(tokenized_column_return, tokenized_glossary_return)
            score2 = similarity_measure.get_sim_score(q_gram_tokenizer.tokenize(column.replace("_","")),q_gram_tokenizer.tokenize(glossary_term))
            score3 = tf_idf.get_sim_score(tokenized_column_return, tokenized_glossary_return)
            output_list.append((column,glossary_term,score,""))
            output_list_qgram.append((column,glossary_term,score2,""))
            output_list_tfidf.append((column, glossary_term, score3,""))

            ### New code
            if column in corresponding_bgs_terms_dict.keys():
                corresponding_glossary_terms =  corresponding_bgs_terms_dict[column]
                for corresponding_glossary_term in corresponding_glossary_terms:
                    _, tokenized_glossary_return = stemming_code_on_words(glossary_term, column, delim_tok, space_tokenizer)
                    _, tokenized_corresponding_glossary_return = stemming_code_on_words(corresponding_glossary_term, column, delim_tok, space_tokenizer)
                    score = similarity_measure.get_sim_score(tokenized_corresponding_glossary_return, tokenized_glossary_return)
                    score2 = similarity_measure.get_sim_score(q_gram_tokenizer.tokenize(corresponding_glossary_term),
                                                              q_gram_tokenizer.tokenize(glossary_term))
                    score3 = tf_idf.get_sim_score(tokenized_corresponding_glossary_return, tokenized_glossary_return)
                    output_list_bg.append((column, glossary_term, score,corresponding_glossary_term))
                    output_list_qgram_bg.append((column, glossary_term, score2,corresponding_glossary_term))
                    output_list_tfidf_bg.append((column, glossary_term, score3,corresponding_glossary_term))



    result_list = [item for item in output_list if item[2] >= prediction_score_of_words_tokens]
    result_list_qgram = [item for item in output_list_qgram if item[2] >= prediction_score_of_qgram]
    result_list_tf_idf = [item for item in output_list_tfidf if item[2] >= prediction_score_of_tfidf]
    print("Predictions From Token Words: "+str(len(result_list)))
    print("Predictions From qgram Words: "+str(len(result_list_qgram)))
    print("Predictions From tf_idf Words: "+str(len(result_list_tf_idf)))

    ### Merging
    result_list = intersect(result_list,result_list_qgram)
    result_list = intersect(result_list,result_list_tf_idf)
    # print("just a check: " + str(len(result_list)))
    result_list = find_unique(result_list)
    # print("just a check: " + str(len(result_list)))


    result_list_bg = [item for item in output_list_bg if item[2] >= prediction_score_of_words_tokens_bg]
    result_list_qgram_bg = [item for item in output_list_qgram_bg if item[2] >= prediction_score_of_qgram_bg]
    result_list_tf_idf_bg = [item for item in output_list_tfidf_bg if item[2] >= prediction_score_of_tfidf_bg]
    print("Predictions From Token Words when Matching with Business Glossary: "+str(len(result_list_bg)))
    print("Predictions From qgram Words when Matching with Business Glossary: "+str(len(result_list_qgram_bg)))
    print("Predictions From tf_idf Words when Matching with Business Glossary: "+str(len(result_list_tf_idf_bg)))

    ### Merging
    result_list_bg = intersect(result_list_bg,result_list_qgram_bg)
    result_list_bg = intersect(result_list_qgram_bg,result_list_tf_idf_bg)
    result_list_bg = find_unique(result_list_bg)

    result_list = merge(result_list,result_list_bg)
    print("Final Predictions:" + str(len(result_list)))

    values_tested = 0
    match_found = 0

    expected_matches = 0
    for key in gold_dict.keys():
        values = gold_dict[key]
        glossary_id = key
        for value in values:
            expected_matches = expected_matches+1

    for result in result_list:
        column_name = result[0]
        glossary_term = result[1]
        glossary_id = bg_terms_id_dict[glossary_term]
        if glossary_id in gold_dict.keys():
            expected_column_name = gold_dict[glossary_id]
            expected_column_name = [expected_column_name_tmp.lower() for expected_column_name_tmp in expected_column_name]
            if(column_name in expected_column_name):
                match_found=match_found+1
                print(column_name)
                print(glossary_term)
                print()

        values_tested=values_tested+1

    print("True Positives: "+str(match_found))
    recall = float(match_found)/float(expected_matches)
    preicsion = float(match_found)/float(values_tested)
    f1 = (2*preicsion*recall)/(preicsion+recall)
    recall_String = "Recall: "+ str(recall)
    preicsion_String = "Precision: "+str(preicsion)
    f1_String = "F1: "+str(f1)
    print(recall_String)
    print(preicsion_String)
    print(f1_String)
    print("Total expected matches: "+ str(expected_matches))