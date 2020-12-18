from bs4 import BeautifulSoup
from os import listdir
from os.path import isfile, join
import zillow.zillow_functions
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
                dom = zillow.zillow_functions.record_xml_util(property_page_soup)
                house_id = file.split('.')[0];
                if not os.path.exists('zillow_xml_from_html'):
                    os.makedirs('zillow_xml_from_html')
                output_path = "zillow_xml_from_html/" + str(house_id) + ".xml"
                f = open(output_path, "w")
                f.write(dom.toprettyxml())
                f.close()
                successful = successful+1

        except:
           print("Error for file: "+str(html_file))
           failure = failure+1
    print(successful)
    print(failure)

from_html_files_to_xml('zillow_html/')
