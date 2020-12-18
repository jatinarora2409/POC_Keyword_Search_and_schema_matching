
from find_similarity_util import *

### Number of predictions:

## For Union
# prediction_score_of_words_tokens = 0.8
# prediction_score_of_qgram = 0.7
# prediction_score_of_tfidf = 0.7


## For Interesection
prediction_score_of_words_tokens = 0.5
prediction_score_of_qgram = 0.3
prediction_score_of_tfidf = 0.58

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
sources = ["_zillow","_redfin","_realtor","_remax"]
#sources = ["_redfin"]

for source in sources:
    print("\n")
    print("Source: "+source)
    schema_file = 'Schema'+source+'.txt'
    business_glossary_file = 'BusinessGlossary'+source+'.xml'
    assignment_capture_file = 'AssignmentCapture'+source+'.csv'
    set_of_columns = read_schema(schema_file)

    column_csv = open('columns' + source + '.csv', "w")
    column_csv.write("Column_name"+"\n")
    for column in set_of_columns:
        column_csv.write(str(column) + "\n")
    column_csv.close()


    set_of_business_glossary,bg_terms_id_dict = get_businesss_glossary(business_glossary_file)

    business_glossary_csv = open('business_glossary' + source + '.csv', "w")
    business_glossary_csv.write("Business Term" + "\n")
    for b_term in set_of_business_glossary:
        business_glossary_csv.write(str(b_term) + "\n")
    business_glossary_csv.close()

    gold_dict = get_assignment_capture_gold(assignment_capture_file)

    output_list = []
    output_list2 = []

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

    output_list_qgram = []
    output_list_tfidf = []
    for column in set_of_columns:
        for glossary_term in set_of_business_glossary:
            tokenized_column_return ,tokenized_glossary_return = stemming_code_on_words(glossary_term, column, delim_tok, space_tokenizer)
            score = similarity_measure.get_sim_score(tokenized_column_return, tokenized_glossary_return)
            score2 = similarity_measure.get_sim_score(q_gram_tokenizer.tokenize(column.replace("_","")),q_gram_tokenizer.tokenize(glossary_term))
            score3 = tf_idf.get_sim_score(tokenized_column_return, tokenized_glossary_return)

            output_list.append((column,glossary_term,score))
            output_list2.append((tokenized_column_return, tokenized_glossary_return, score))
            output_list_qgram.append((column,glossary_term,score2))
            output_list_tfidf.append((column, glossary_term, score3))

    ## Tokenizing on words
    output_list = Sort_Tuple(tup=output_list)
    output_list2 = Sort_Tuple(tup=output_list2)
    output_file = open('output'+source+'.txt', "w")
    for element in output_list:
        output_file.write(str(element)+"\n")
    output_file.close()

    ## Tokenizing on qgram
    output_list_qgram = Sort_Tuple(tup=output_list_qgram)
    output_file = open('output_gram'+source+'.txt', "w")
    for element in output_list_qgram:
        output_file.write(str(element)+"\n")
    output_file.close()

    ## Tokenizing on qgram
    output_list_tfidf = Sort_Tuple(tup=output_list_tfidf)
    output_file = open('output_tfidf'+source+'.txt', "w")
    for element in output_list_tfidf:
        output_file.write(str(element)+"\n")
    output_file.close()

    result_list = [item for item in output_list if item[2] >= prediction_score_of_words_tokens]
    result_list_qgram = [item for item in output_list_qgram if item[2] >= prediction_score_of_qgram]
    result_list_tf_idf = [item for item in output_list_tfidf if item[2] >= prediction_score_of_tfidf]
    ### Merging
    print("Predictions From Token Words: "+str(len(result_list)))
    print("Predictions From qgram Words: "+str(len(result_list_qgram)))
    print("Predictions From tf_idf Words: "+str(len(result_list_tf_idf)))

    #result_list = merge(result_list,result_list_qgram)
    result_list = intersect_small(result_list,result_list_qgram)
    result_list = intersect_small(result_list,result_list_tf_idf)

    #result_list = result_list_tf_idf
    print("Final Predictions:" + str(len(result_list)))
    values_tested = 0
    match_found = 0

    expected_matches = 0
    for key in gold_dict.keys():
        values = gold_dict[key]
        glossary_id = key
        for value in values:
            expected_matches = expected_matches+1

    output_file_matches = open('output_matches' + source + '.txt', "w")
    for result in result_list:
        column_name = result[0]
        glossary_term = result[1]
        glossary_id = bg_terms_id_dict[glossary_term]
        if glossary_id in gold_dict.keys():
            expected_column_name = gold_dict[glossary_id]
            expected_column_name = [expected_column_name_tmp.lower() for expected_column_name_tmp in expected_column_name]
            if(column_name in expected_column_name):
                match_found=match_found+1
                output_file_matches.write("column_name: " +str(delim_tok.tokenize(column_name))+ "\n")
                output_file_matches.write("glossary_term: "+str(space_tokenizer.tokenize(glossary_term))+"\n")
                output_file_matches.write("\n")

        #     else:
        #           print("column_name: " + str(delim_tok.tokenize(column_name)))
        #           print("glossary_term: " + str(space_tokenizer.tokenize(glossary_term)))
        #           print("Expected_column_name " + str(expected_column_name))
        #           print("")
        # else:
        #       print("column_name: " + str(delim_tok.tokenize(column_name)))
        #       print("glossary_term: " + str(space_tokenizer.tokenize(glossary_term)))
        #       print("")
        values_tested=values_tested+1
    output_file_matches.close()

    output_file_2 = open('output_2'+source+'.txt', "a")
    print("True Positives: "+str(match_found))
    recall = float(match_found) / float(expected_matches)
    preicsion = float(match_found) / float(values_tested)
    f1 = (2 * preicsion * recall) / (preicsion + recall)
    recall_String = "Recall: " + str(recall)
    preicsion_String = "Precision: " + str(preicsion)
    f1_String = "F1: " + str(f1)
    print(recall_String)
    print(preicsion_String)
    print(f1_String)
    output_file_2.write("Measure: "+measure+"\n")
    #output_file_2.write("Threshold: "+str(threshold)+"\n")
    output_file_2.write("tokenizer:" +str(tokenizer_string)+"\n")
    output_file_2.write(preicsion_String+"\n")
    output_file_2.write(recall_String+"\n")
    output_file_2.write("\n")



    print("Total expected matches: "+ str(expected_matches))

    capture_csv = open('capture' + source + '.csv', "w")
    capture_csv.write("Column Name,Business Term"+"\n")
    output_missed_matches = open('output_missed_matches' + source + '.txt', "w")
    for key in gold_dict.keys():
        values = gold_dict[key]
        glossary_id = key
        for value in values:
            glossary_term = get_key(bg_terms_id_dict,glossary_id)
            column_name = value
            capture_csv.write(str(column_name)+","+str(glossary_term)+"\n")
            checking_result = check_in_result(result_list,glossary_term,column_name)
            if not checking_result:
                output_missed_matches.write("C: " +str((column_name))+"\n")
                output_missed_matches.write("G: "+str((glossary_term))+"\n")
                output_missed_matches.write("\n")

    output_missed_matches.close()
    capture_csv.close()
