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


def get_list_of_business_glossary_term(assignment_file, business_glossary_file, column_name):
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


def get_column_dict(source):
    zillow_assignment = 'AssignmentCapture_zillow.csv'
    realtor_assignment = 'AssignmentCapture_realtor.csv'
    redfin_assignment = 'AssignmentCapture_redfin.csv'
    remax_assignment = 'AssignmentCapture_remax.csv'

    zillow_business_glossary_xml = 'BusinessGlossary_zillow.xml'
    realtor_business_glossary_xml = "BusinessGlossary_realtor.xml"
    remax_business_glossary_xml = 'BusinessGlossary_remax.xml'
    redfin_business_glossary_xml = 'BusinessGlossary_redfin.xml'

    schema_matching_file = "Schema_Matching.csv"

    import csv

    with open(schema_matching_file) as csvfile:
        readCSV = csv.reader(csvfile, delimiter=',')
        zillow = []
        realtor = []
        remax = []
        redfin = []
        flag = False
        for row in readCSV:
            if not flag:
                flag = True
                continue
            w = row[1]
            if w != "":
                w = row[1].split(".", 1)[1]
            zillow.append(w)
            w = row[2]
            if w != "":
                w = row[2].split(".", 1)[1]
            realtor.append(w)
            w = row[3]
            if row[3] != "":
                w = row[3].split(".", 1)[1]
            remax.append(w)
            w = row[4]
            if row[4] != "":
                w = row[4].split(".", 1)[1]
            redfin.append(w)

    source_dict = dict()
    if source == "zillow":
        for i in range(len(zillow)):
            bg_terms_list = []
            if zillow[i] != "":
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(realtor_assignment, realtor_business_glossary_xml, realtor[i]))
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(redfin_assignment, redfin_business_glossary_xml, redfin[i]))
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(remax_assignment, remax_business_glossary_xml, remax[i]))
                source_dict[zillow[i]] = bg_terms_list

        #print(source_dict)
    if source == "remax":
        for i in range(len(remax)):
            bg_terms_list = []
            if remax[i] != "":
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(realtor_assignment, realtor_business_glossary_xml, realtor[i]))
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(redfin_assignment, redfin_business_glossary_xml, redfin[i]))
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(zillow_assignment, zillow_business_glossary_xml, zillow[i]))
                source_dict[remax[i]] = bg_terms_list

    if source == "redfin":
        for i in range(len(redfin)):
            bg_terms_list = []
            if redfin[i] != "":
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(realtor_assignment, realtor_business_glossary_xml, realtor[i]))
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(zillow_assignment, zillow_business_glossary_xml, zillow[i]))
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(remax_assignment, remax_business_glossary_xml, remax[i]))
                source_dict[redfin[i]] = bg_terms_list

    if source == "realtor":
        for i in range(len(zillow)):
            bg_terms_list = []
            if realtor[i] != "":
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(zillow_assignment, zillow_business_glossary_xml, zillow[i]))
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(redfin_assignment, redfin_business_glossary_xml, redfin[i]))
                bg_terms_list.extend(
                    get_list_of_business_glossary_term(remax_assignment, remax_business_glossary_xml, remax[i]))
                source_dict[realtor[i]] = bg_terms_list

    return source_dict


# if __name__ == '__main__':
#     get_column_dict("zillow")
