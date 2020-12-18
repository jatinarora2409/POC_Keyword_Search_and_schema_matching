from bs4 import BeautifulSoup
import requests
import redfin_functions

import zillow.zillow_functions
#property_link='https://www.zillow.com/homedetails/914-S-30th-Dr-Phoenix-AZ-85009/7515587_zpid/'
property_link='https://www.redfin.com/WI/Sun-Prairie/1160-Heritage-Ct-53590/home/57978317'
headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_4) AppleWebKit/534.26 (KHTML, like Gecko) Chrome/24.0.453.102'}
page = requests.post(property_link, headers=headers)
property_page_soup = BeautifulSoup(page.content, 'html.parser')
print(redfin_functions.record(property_link))

