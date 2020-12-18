
from find_similarity_util import *
from SchemaMatching import get_column_dict


query1 = 'area of the house'
## Zillow Results: 3
## Realtor Results: 2
## Redfin Results: 5
## Remax Results: 4
query2 = "Style of the house"
## Zillow Results: 2
## Realtor Results: 2
## Redfin Results: 3
## Remax Results:2
query3 = 'Remodelling year'
## Zillow Results: 2
## Realtor Results: 3
## Redfin Results: 3
## Remax Results: 1
query4 = 'description of the house'
## Zillow Results: 1
## Realtor Results:1
## Redfin Results: 0
## Remax Results:1
query = query2
source = 'remax'
business_glossary_use = True

schema_path_file = 'Schema_'+source+'.txt'
assignment_capture_file = 'AssignmentCapture_'+source+'.csv'
business_glossary_file = 'BusinessGlossary_'+source+'.xml'

if(not business_glossary_use):
    assignment_capture_file = None
    business_glossary_file = None

output_count = 5

import csv
import xml.etree.ElementTree as ET


def get_bg_from_term_id(term_id, business_glossary_file):
    tree = ET.parse(business_glossary_file)
    root = tree.getroot()

    bg = root.findall('term')

    for x in bg:
        if x.find('id').text == term_id:
            return x.find('value').text


def get_term_id_for_column(column_name, file):
    with open(file) as csvfile:
        readCSV = csv.reader(csvfile, delimiter=',')
        flag = False
        for row in readCSV:
            if not flag:
                flag = True
                continue
            column = row[1].split('.', 1)[1]
            if column == column_name:
                return row[2].split(',')


def get_list_of_business_glossary_term_local(assignment_file, business_glossary_file, column_name):
    # Get term id's related to this column
    list_of_terms = []
    #print(column_name)
    w = get_term_id_for_column(column_name, assignment_file)
    if w is not None:
        list_of_terms.extend(w)  # this contains list of termIDs

    # Get column names for the term id's
    bg = []
    for x in list_of_terms:
        bg.append(get_bg_from_term_id(x, business_glossary_file))
    return bg


def make_corpus(source,set_of_columns,business_glossary_file=None,assignment_capture_file=None):
    print("Query: "+ str(query))
    print("Expected Count:" +str(output_count))
    print
    delim_tok = sm.DelimiterTokenizer(delim_set=['_'])
    space_tokenizer = sm.WhitespaceTokenizer()
    corpus_list = []
    corpus_dict = dict()
    bg_glossary_dict = get_column_dict(source)

    for key in set_of_columns:
            glossary_term = " "
            values = set_of_columns[key]
            tokenized_table_return,_ = stemming_code_on_words(glossary_term, key,delim_tok, space_tokenizer)
            for value in values:
                tokenized_column_return, _ = stemming_code_on_words(glossary_term, value,delim_tok, space_tokenizer)
                corpus_item = []
                corpus_item.extend(tokenized_table_return)
                corpus_item.extend(tokenized_column_return)
                if business_glossary_file is not None and assignment_capture_file is not None:
                    #business_glossary_terms_list = get_list_of_business_glossary_term(assignment_capture_file,business_glossary_file,value)
                    if value in bg_glossary_dict.keys():
                        business_glossary_terms_list = bg_glossary_dict[value]
                        if value == 'lot_size':
                            print(str(business_glossary_terms_list))
                        for business_glossary_term in business_glossary_terms_list:
                            _, tokenized_glossary_return = stemming_code_on_words(business_glossary_term, " ", delim_tok,
                                                                                space_tokenizer)
                            corpus_item.extend(tokenized_glossary_return)
                #print(corpus_item)
                corpus_dict[str(corpus_item)] = (key,value)
                #TODO: Add business Glossary Term as well
                corpus_list.append((corpus_item))
    return corpus_list,corpus_dict

def sort_output_list(tup):
    # reverse = None (Sorts in Ascending order)
    # key is set to sort using second element of
    # sublist lambda has been used
    return (sorted(tup, key=lambda x: x[1],reverse=True))


def keyword_search(source,query,schema_path_file,business_glossary_file=None,assignment_capture_file=None):
    set_of_columns = read_schema_with_tablenames(schema_path_file)
    corpus_list,corpus_dict = make_corpus(source,set_of_columns,business_glossary_file = business_glossary_file,assignment_capture_file=assignment_capture_file)
    tf_idf = sm.TfIdf(corpus_list=corpus_list)
    delim_tok = sm.DelimiterTokenizer(delim_set=['_'])
    space_tokenizer = sm.WhitespaceTokenizer()
    _,tokenized_query_return = stemming_code_on_words(query," ",delim_tok, space_tokenizer)
    output = []
    for item in corpus_list:
        score = tf_idf.get_sim_score(tokenized_query_return, item)
        output.append((item,score))
    output = sort_output_list(output)
    #print(output)

    for i in range(output_count):
        output_corpus_item = output[i][0]
        score = output[i][1]
        (table_name,column_name) = corpus_dict[str(output_corpus_item)]
        print("Table Name: "+str(table_name))
        print("Column Name: "+str(column_name))
        print("Score: "+str(score))
        print


keyword_search(source,query,schema_path_file,business_glossary_file,assignment_capture_file)
