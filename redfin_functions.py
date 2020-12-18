from bs4 import BeautifulSoup
import requests
import dicttoxml
from xml.dom.minidom import parseString


def get_homemain_stats(pageSoup):
    homemain_stats = dict()
    div_homeMain = pageSoup.find('div',class_='HomeMainStats home-info inline-block float-right')
    all_dives = div_homeMain.find_all('div',recursive=False)
    for div in all_dives:
        key = div.find('span',class_='statsLabel')
        if key is None:
            key = div.find('div',class_='statsLabel')

        value = div.find('div',class_='statsValue')
        if value is None:
            value = div.find('span',class_='statsValue')
        homemain_stats[key.text] = value.text
    return homemain_stats


def get_price_insights(pageSoup):
    key_details_hash = dict()
    bhi_class = pageSoup.find('div', class_='bhi')
    key_details_lists = bhi_class.find_all('div', class_='keyDetailsList')
    key_detail_list = key_details_lists[0]
    key_detail_divs = key_detail_list.find_all('div',class_='keyDetail font-weight-roman font-size-base')
    for key_detail_div in key_detail_divs:
        key = key_detail_div.find('span',class_='header font-color-gray-light inline-block').text
        value = key_detail_div.find('span', class_='content text-right').text
        key_details_hash[key] = value
    return key_details_hash

def get_home_facts(pageSoup):
    key_details_hash = dict()
    bhi_class = pageSoup.find('div', class_='bhi')
    key_details_lists = bhi_class.find_all('div', class_='keyDetailsList')
    key_detail_list = key_details_lists[1]
    key_detail_divs = key_detail_list.find_all('div',class_='keyDetail font-weight-roman font-size-base')
    for key_detail_div in key_detail_divs:
        key = key_detail_div.find('span',class_='header font-color-gray-light inline-block').text
        value = key_detail_div.find('span', class_='content text-right').text
        key_details_hash[key] = value
    return key_details_hash

def get_description(pageSoup):
    bhi_class = pageSoup.find('div', class_='bhi')
    sectionContentList = bhi_class.find_all('div', class_='sectionContent')
    return sectionContentList[0].text

def get_public_facts(pageSoup):
    facts = dict()
    rows_in_facts = pageSoup.find('div',class_="facts-table").find_all('div',class_="table-row")
    for row in rows_in_facts:
        key = row.find('span',class_="table-label").text
        value = row.find('div',class_='table-value').text
        facts[key]=value
    return facts

def get_address(pageSoup):
    address = dict()
    street_address = pageSoup.find('div',class_='top-stats').find('span',class_='street-address').text
    locality = pageSoup.find('div',class_='top-stats').find('span',class_='locality').text
    region = pageSoup.find('div', class_='top-stats').find('span', class_='region').text
    postal_code = pageSoup.find('div', class_='top-stats').find('span', class_='postal-code').text
    address['street-address'] = street_address
    address['locality'] = locality
    address['region'] = region
    address['postal-code'] = postal_code
    return address


class AnItem:
  def __init__(self, heading, group_data):
    self.heading = heading
    self.group_data = group_data

def get_data_from_amenties(group_data):
    data = list()
    amenity_groups = group_data.find_all('div',class_='amenity-group')
    for amenity_group in amenity_groups:
        heading = amenity_group.find('h3').text
        entries = amenity_group.find_all('li',class_ = 'entryItem')
        text = []
        for entry in entries:
            text.append(entry.text)
        data.append(AnItem(heading,text).__dict__)
    return data

class Tuple:
  def __init__(self, heading, group_data):
    self.heading = heading
    self.details = group_data

class Property_detail_list:
    def __init__(self):
        self.my_list = []

def get_Property_Details(pageSoup):
    property_details = list()
    amenties_container = pageSoup.find('div',class_='amenities-container')
    group_titles = amenties_container.find_all('div', class_='super-group-title')
    group_datas = amenties_container.find_all('div', class_='super-group-content')

    for i in range(0,len(group_titles)):
        group_title = group_titles[i]
        group_data = get_data_from_amenties(group_datas[i])
        property_details.append(Tuple(group_title.text, group_data).__dict__)
    return property_details


## Not Working Yet.
def get_SalesHistory(pageSoup):
    saleHistory_data = []
    salesHistory = pageSoup.find_all('div',class_='timeline-content')
    for saleHistory in salesHistory:
        dict_data = dict()
        date_and_price = saleHistory.find_all('div',class_='col=4')
        descriptions = saleHistory.find_all('div',class_='description-col col-4').find_all('div')
        description_string = ""
        for description in descriptions:
            description_string = description_string + description.text
        description_detail = saleHistory.find_all('div',class_='description-col col-4').find('p',class_="subtext").text
        dict_data['date'] = date_and_price[0].find('p').text
        dict_data['descrpition'] = description_detail
        dict_data['price'] = date_and_price[1].find('div',class_='price-col number').text
        saleHistory_data.append(dict_data)
    return saleHistory_data

def get_listed_by_details(page_soup):
    agent_details = dict()
    agent_basic_detail = page_soup.find('div', class_='agent-basic-details font-color-gray-dark').find('a')
    if(agent_basic_detail is not None and 'href' in agent_basic_detail.keys()):
       link = agent_basic_detail['href']
       agent_details['listed_by_page'] = link

    name = page_soup.find('div', class_='agent-basic-details font-color-gray-dark').find('span',recursive=False).find('span',recursive=False)
    if(name is not None):
        name= name.text
        agent_details['name'] = name

    agentLicenseDisplay = page_soup.find('div',class_='agent-basic-details font-color-gray-dark').find('span',class_='agentLicenseDisplay')
    if(agentLicenseDisplay is not None):
        agentLicenseDisplay = agentLicenseDisplay.text
        agent_details['agentLicenseDisplay'] = agentLicenseDisplay

    org = page_soup.find('div', class_='agent-basic-details font-color-gray-dark').find('span', recursive=False).find_all('span',recursive=False)
    org = org[len(org)-1]
    if (org is not None):
        org = org.text[2:len(org.text)-1]
        agent_details['org'] = org
    return agent_details

def record_util(property_page_soup):
    record_xml = dict()

    address = get_address(property_page_soup)
    record_xml['HomeInfo_Address'] = address

    main_stats = get_homemain_stats(property_page_soup)
    record_xml['main_stats'] = main_stats

    description = get_description(property_page_soup)
    record_xml['text_base_description'] = description

    listed_by_data = get_listed_by_details(property_page_soup)
    record_xml['listed_by'] = listed_by_data

    price_insights = get_price_insights(property_page_soup)
    record_xml['price_insights'] = price_insights

    home_facts = get_home_facts(property_page_soup)
    record_xml['home_facts'] = home_facts

    property_details = get_Property_Details(property_page_soup)
    record_xml['property_details'] = property_details

    public_facts = get_public_facts(property_page_soup)
    record_xml['public_facts'] = public_facts
    my_item_func = lambda x: x[:-1]
    xml = dicttoxml.dicttoxml(record_xml, attr_type=False, custom_root='home',item_func=my_item_func)
    dom = parseString(xml)
    return dom

def record(property_link,save_xml=True):
    headers = {'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'}
    page = requests.post(property_link, headers=headers)
    property_page_soup = BeautifulSoup(page.content, 'html.parser')
    dom = record_util(property_link)
    parts_of_url = property_link.split("/")
    if save_xml:
        output_path = "redfin_data/" + str(parts_of_url[len(parts_of_url)-1])+".xml"
        f = open(output_path, "w")
        f.write(dom.toprettyxml())
        f.close()
    output_path = "redfin_html/" + str(parts_of_url[len(parts_of_url)-1])+".html"
    with open(output_path, "w") as k:
        k.write(str(property_page_soup))
#
# link = 'https://www.redfin.com/WI/Madison/309-W-Washington-Ave-53703/unit-314/home/89951364'
# record(link)