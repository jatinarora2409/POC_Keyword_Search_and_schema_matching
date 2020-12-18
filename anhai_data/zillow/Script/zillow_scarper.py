
from bs4 import BeautifulSoup
import requests
import sys
import time
import random
import traceback
import zillow.zillow_functions
import os
#Completed
#zipcodes = "94101,53703,20015,90046"
#zipcodes = "85009,75217,78735,91945"
#zipcodes = "60612,53226,95050,90032"
zipcodes = "32006,75011,20601,60176"

base_URL = 'https://www.zillow.com'
headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'}
zipcodes = zipcodes.split(',')
properties_list = []
for zipcode in zipcodes:
    zip_code_url = base_URL + '/homes/'+zipcode+"_rb/"
    page = requests.post(zip_code_url,headers=headers)
    mainPageSoup = BeautifulSoup(page.content, 'html.parser')
    total_number_of_houses = 0
    homeCardContainers = mainPageSoup.find_all(class_='list-card-info')
    for homeCardContainer in homeCardContainers:
        link = homeCardContainer.find('a')['href']
        properties_list.append(link.strip())
        total_number_of_houses=total_number_of_houses+1
    print("Total Houses for PinCode: "+zipcode + " are " + str(total_number_of_houses))
    time.sleep(30)

## Randomize and Take Unique
random.shuffle(properties_list)
print("Current Length: " + str(len(properties_list)))
properties_list = set(properties_list)
print("Unique Values: " + str(len(properties_list)))

progress_count = 0
successful = 0
failure = 0



for property_link in properties_list:
    progress_count = progress_count+1
    try:
        time.sleep(30)
        zillow.zillow_functions.record(property_link,save_xml=False)
        successful = successful+1
    except:
        e = sys.exc_info()[0]
        parts_of_url = property_link.split("/")
        parts_of_url = parts_of_url[len(parts_of_url) - 2].split("_")
        if not os.path.exists('zillow_failure'):
            os.makedirs('zillow_failure')
        output_path = "zillow_failure/" + str(parts_of_url[0]) + ".xml"
        f = open(output_path, "w")
        f.write("Something went wrong with: " + property_link)
        f.write(traceback.format_exc())
        f.close()
        failure = failure+1
    finally:
        if((progress_count)%5==1):
            print("Progress Count: " + str(progress_count))
            print("Successful: " + str(successful))
            print("Failure: " + str(failure))
            print()

