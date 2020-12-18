from bs4 import BeautifulSoup
from os import listdir
from os.path import isfile, join
import redfin_functions
import os

def from_html_files_to_xml(path):
    html_files = [(f, join(path, f)) for f in listdir(path) if isfile(join(path, f))]
    if ('.DS_Store', join(path, '.DS_Store')) in html_files:
        html_files.remove(('.DS_Store', join(path, '.DS_Store')))

    successful = 0

    failure = 0
    for (file, html_file) in html_files:
        try:
            with open(html_file, 'r') as f:
                contents = f.read()
                property_page_soup = BeautifulSoup(contents, 'html.parser')
                dom = redfin_functions.record_util(property_page_soup)
                house_id = file.split('.')[0]
                if not os.path.exists('redfin_xml_from_html'):
                    os.makedirs('redfin_xml_from_html')
                output_path = "redfin_xml_from_html/" + str(house_id) + ".xml"
                f = open(output_path, "w")
                f.write(dom.toprettyxml())
                f.close()
                successful = successful+1

        except Exception as e:
           print(e)
           print("Error for file: "+str(html_file))
           failure = failure+1
    print(successful)
    print(failure)

from_html_files_to_xml('redfin_html/')
