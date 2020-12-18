
from find_similarity_util import *

### Number of predictions:
predictions = 19

##### TOkenizers
qgram_qval = 2
tokenizer = sm.QgramTokenizer(qval=qgram_qval)
tokenizer_string = "QgramTokenizer"

tokenizer2 = sm.WhitespaceTokenizer()
tokenizer_string = "whitespace_tokensizer"
delim_tok = sm.DelimiterTokenizer(delim_set=['_'])

## Similarity_Measures
#similarity_measure = sm.Jaccard()
#measure = "Jaccard"

similarity_measure = sm.OverlapCoefficient()
measure = "OverlapCoefficient"

# similarity_measure = sm.Jaro()
# measure="Jaro"
# similarity_measure = sm.Levenshtein()
# measure="Levenshtein"
#similarity_measure = sm.Cosine()
#measure="Cosine"
# similarity_measure = sm.Dice()
# measure="Dice"
# similarity_measure = sm.TverskyIndex()
# measure="TverskyIndex"
#similarity_measure = sm.TfIdf()
#measure="TfIdf"

set_of_columns = read_schema('Schema.txt')
set_of_business_glossary,bg_terms_id_dict = get_businesss_glossary('BusinessGlossary.xml')
gold_dict = get_assignment_capture_gold('AssignmentCapture.csv')

output_list = []
for column in set_of_columns:
    for glossary_term in set_of_business_glossary:

        #token_matching
        # print(delim_tok.tokenize(column))
        # print(tokenizer2.tokenize(glossary_term))
        #print("\n")

        score = similarity_measure.get_sim_score(delim_tok.tokenize(column), tokenizer2.tokenize(glossary_term))



        #print(delim_tok.tokenize(column))
        #Sequence_matching
        #score = similarity_measure.get_sim_score(column,glossary_term)

        output_string = column+","+glossary_term+","+str(score)
        output_list.append((column,glossary_term,score))



output_list = Sort_Tuple(tup=output_list)
output_file = open('output.txt', "w")
for element in output_list:
    output_file.write(str(element)+"\n")
output_file.close()


## Experiment 1
result_list = output_list[:predictions]
#result_list = output_list
threshold = result_list[len(result_list)-1][2]

print("Threshold is :"+str(threshold))
values_tested = 0
match_found = 0
expected_matches = len(gold_dict)

for result in result_list:
    column_name = result[0]
    glossary_term = result[1]
    glossary_id = bg_terms_id_dict[glossary_term]
    if glossary_id in gold_dict.keys():
        expected_column_name = gold_dict[glossary_id]
        if(column_name in expected_column_name):
            match_found=match_found+1
            #print("column_name: " +str(delim_tok.tokenize(column_name)))
            #print("glossary_term: "+str(tokenizer2.tokenize(glossary_term)))

    #     else:
    #          print("column_name: " + str(delim_tok.tokenize(column_name)))
    #          print("glossary_term: " + str(tokenizer2.tokenize(glossary_term)))
    #          print("Expected_column_name " + str(expected_column_name))
    #          print("")
    # else:
    #      print("column_name: " + str(delim_tok.tokenize(column_name)))
    #      print("glossary_term: " + str(tokenizer2.tokenize(glossary_term)))
    #      print("")
    values_tested=values_tested+1


output_file_2 = open('output_2.txt', "a")
recall_String = "Recall: "+ str(float(match_found)/float(expected_matches))
preicsion_String = "Precision: "+str(float(match_found)/float(values_tested))
print(recall_String)
print(preicsion_String)
output_file_2.write("Measure: "+measure+"\n")
output_file_2.write("Threshold: "+str(threshold)+"\n")
output_file_2.write("tokenizer:" +str(tokenizer_string)+"\n")
output_file_2.write(preicsion_String+"\n")
output_file_2.write(recall_String+"\n")
output_file_2.write("\n")

def get_key(dicti,val):
    for key, value in dicti.items():
         if val == value:
             return key

def check_in_result(result_list,glossary_term,column_name):
    for result in result_list:
        result_column_name = result[0]
        result_glossary_term = result[1]
        if(result_glossary_term==glossary_term and result_column_name==column_name):
            return True
    return False

for key in gold_dict.keys():
    values = gold_dict[key]
    glossary_id = key
    for value in values:
        glossary_term = get_key(bg_terms_id_dict,glossary_id)
        column_name = value
        checking_result = check_in_result(result_list,glossary_term,column_name)
        # if not checking_result:
        #print("C: " +str((column_name)))
        #print("G: "+str((glossary_term)))
        #print("")