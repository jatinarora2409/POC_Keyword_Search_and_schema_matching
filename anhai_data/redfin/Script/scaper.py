
from bs4 import BeautifulSoup
import requests
import redfin_functions
import sys
import time
import random
import traceback

#Completed
#zipcodes =  "94101"
#zipcodes =  "53703"
#zipcodes = "20015,90046"
#zipcodes = "78735,91945"
#zipcodes = "60612,53226"
# zipcodes = "95050,90032"
#zipcodes = "94101,53703,20015,90046"
zipcodes = "85009,75217,78735,91945"

base_URL = 'https://www.redfin.com'
headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'}
zipcodes = zipcodes.split(',')
properties_list = []
for zipcode in zipcodes:
    zip_code_url = base_URL + '/zipcode/'+zipcode
    page = requests.post(zip_code_url,headers=headers)
    mainPageSoup = BeautifulSoup(page.content, 'html.parser')
    number_of_clickable_goToPages = mainPageSoup.find_all(class_='clickable goToPage')
    max_pages = 0
    for number_of_clickable_goToPage in number_of_clickable_goToPages:
        number = int(number_of_clickable_goToPage.text)
        if(number>max_pages):
            max_pages = number
    if(max_pages>7):
        max_pages=7
    total_number_of_houses = 0
    for page_number in range(1,max_pages+1):
        if(page_number>=2):
            page = requests.post(zip_code_url+"/page-"+str(page_number), headers=headers)
            mainPageSoup = BeautifulSoup(page.content, 'html.parser')
        homeCardContainers = mainPageSoup.find_all(class_='MapHomeCardReact HomeCard')
        for homeCardContainer in homeCardContainers:
            link = homeCardContainer.find('a')['href']
            properties_list.append(base_URL+link.strip())
            total_number_of_houses=total_number_of_houses+1
    print("Total Houses for PinCode: "+zipcode + " are " + str(total_number_of_houses))
    time.sleep(15)

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
        redfin_functions.record(property_link,save_xml=False)
        time.sleep(30)
        successful = successful+1
    except:
        e = sys.exc_info()[0]
        parts_of_url = property_link.split("/")
        output_path = "redfin_failure/" + str(parts_of_url[len(parts_of_url) - 1]) + ".xml"
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

