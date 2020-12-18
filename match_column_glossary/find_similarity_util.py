import xml.etree.ElementTree as ET
import py_stringmatching as sm
import csv

def read_schema(path):
    f = open(path, "r")
    set_of_columns = set()
    Lines = f.readlines()
    # Lines = Lines[0].split('\r')
    for line in Lines:

        start = line.find('(')+1
        end = line.find(')')
        columns = line[start:end].split(",")
        columns = [column.lower() for column in columns]
        set_of_columns.update(columns)
    return set_of_columns


def read_schema_with_tablenames(path):
    f = open(path, "r")
    set_of_columns = dict()
    Lines = f.readlines()
    # Lines = Lines[0].split('\r')
    for line in Lines:
        start = line.find('(') + 1
        end = line.find(')')
        table_name =line[0:start-1]
        columns = line[start:end].split(",")
        columns = [column.lower() for column in columns]
        set_of_columns[table_name] = columns
    return set_of_columns

def Sort_Tuple(tup):
    # reverse = None (Sorts in Ascending order)
    # key is set to sort using second element of
    # sublist lambda has been used
    return (sorted(tup, key=lambda x: x[2],reverse=True))


def get_businesss_glossary(business_glossary_file):
    tree = ET.parse(business_glossary_file)
    root = tree.getroot()
    bg = root.findall('term')
    bg_terms = []
    bg_terms_id_dict = dict()
    for x in bg:
        bg_terms.append(x.find('value').text.lower())
        bg_terms_id_dict[x.find('value').text.lower()] = x.find('id').text.lower()
    return bg_terms,bg_terms_id_dict

def get_assignment_capture_gold(path):
    with open(path) as csvfile:
        readCSV = csv.reader(csvfile, delimiter=',')
        terms_ID = []
        column_name = []
        for row in readCSV:
            terms = row[2]
            column = row[1]

            terms_ID.append(terms)
            column_name.append(column)

        terms_ID.pop(0)
        column_name.pop(0)

        # print(terms_ID)
        # print(column_name)

        column_name_refined = []

        for x in column_name:
            column_name_refined.append(x.split('.')[1])
        column_name = column_name_refined

        term_column_dict = dict()
        for i in range(len(terms_ID)):
            if terms_ID[i] not in term_column_dict.keys():
                term_column_dict[terms_ID[i]] = []
            term_column_dict[terms_ID[i]].append(column_name[i])

        return term_column_dict

def stemming_code_on_words(glossary_term,column_term,delim_tok,space_tokenizer):
    glossary_term = glossary_term.lower();
    column_term = column_term.lower();
    tokenized_column = delim_tok.tokenize(column_term)
    tokenized_glossary = space_tokenizer.tokenize(glossary_term)
    import nltk.stem
    ps = nltk.stem.PorterStemmer()

    tokenized_column_return = [ps.stem(item) for item in tokenized_column]
    tokenized_glossary_return = [ps.stem(item) for item in tokenized_glossary]

    return tokenized_column_return ,tokenized_glossary_return

def merge(lst1, lst2):
    result = lst2

    for a in lst1:
        found=False
        for b in lst2:
            if a[0] == b[0] and a[1] == b[1]:
                found= True
                break
        if found==False and a not in result:
            result.append(a)
    return result

def intersect_small(lst1, lst2):
    result_list = []
    for a in lst1:
        for b in lst2:
            if a[0] == b[0] and a[1] == b[1]:
                if a not in result_list:
                    result_list.append(a)
    return result_list


def intersect(lst1, lst2):
    result_list = []
    for a in lst1:
        for b in lst2:
            if a[0] == b[0] and a[1] == b[1] and a[3]==b[3]:
                if a not in result_list:
                    result_list.append(a)
    return result_list

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

def find_unique(list1):
    result_list = []
    for item in list1:
        if (item[0],item[1]) not in result_list:
            result_list.append((item[0],item[1]))
    return result_list