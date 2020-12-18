from bs4 import BeautifulSoup
import dicttoxml
from xml.dom.minidom import parseString
from urllib.request import Request, urlopen
import json
import os

class Tuple:
  def __init__(self, heading, group_data):
    self.heading = heading
    self.details = group_data

def get_summary(pageSoup):
    home_details_dict = dict()
    home_details = pageSoup.find('div',class_='ds-home-details-chip')
    price = home_details.find('h3',class_='ds-price').text
    home_details_dict['price'] = price
    span_bed_bath_la = home_details.find_all('span', class_='ds-bed-bath-living-area')
    bd = span_bed_bath_la[0].find_all('span',recursive=False)[0].text
    ba = span_bed_bath_la[1].find_all('span',recursive=False)[0].text
    area = span_bed_bath_la[2].find_all('span',recursive=False)[0].text
    home_details_dict['bd'] = bd
    home_details_dict['ba'] = ba
    home_details_dict['area'] = area
    return home_details_dict

def get_address(pageSoup):
    home_details = pageSoup.find('div',class_='ds-home-details-chip')
    address = dict()
    street_address = home_details.find('h1',class_='ds-address-container').find_all('span')[0].text
    city_state = home_details.find('h1',class_='ds-address-container').find_all('span')[1].text
    address['street_address'] =street_address
    address['city_state'] = city_state
    return address

def get_overview(pageSoup):
    overview = pageSoup.find('div',class_='ds-overview-section')
    return overview.text

# Work with selinum
def get_listing_agent(pageSoup):
    content_item = str(pageSoup)
    # print(content_item)
    listing_provider_starting = content_item.find('listingProvider')
    start = content_item.find('{',listing_provider_starting)
    end = content_item.find('}',content_item.find('lotAreaValue',listing_provider_starting)-5)
    # print(content_item[end])
    # print(content_item[end+1])
    listing_string = (content_item[start:(end+1)]).strip()
    listing_string = listing_string.encode('utf-8').decode('unicode_escape')
    listing_agent_details = json.loads(listing_string)
    listing_agent_details_final = dict()
    listing_agent_details_final['agentName'] = listing_agent_details['agentName']
    listing_agent_details_final['phoneNumber'] = listing_agent_details['phoneNumber']
    listing_agent_details_final['agentLicenseNumber'] = listing_agent_details['agentLicenseNumber']
    listing_agent_details_final['postingGroupName'] = listing_agent_details['postingGroupName']
    listing_agent_details_final['postingWebsiteLinkText'] = listing_agent_details['postingWebsiteLinkText']

    return listing_agent_details_final
    # .find('div',class_='zsg-content-item').find('div',class_='ds-body-small')
    # list_of_spans  =  content_item.find_all('span',recursive=False)
    #
    # listing_agent = dict()
    # listing_agent['display-name'] = list_of_spans[0].text
    # listing_agent['brokerage_name'] = list_of_spans[1].text
    # listing_agent['phone number'] = list_of_spans[2].text
    # listing_agent['source'] = list_of_spans[3].text
    # return listing_agent

def get_facts(pageSoup):
    list_of_features = pageSoup.find_all('li',class_='ds-home-fact-list-item')
    facts_dict = dict()
    for facts in list_of_features:
        fact_label = facts.find('span',class_='ds-standard-label ds-home-fact-label').text
        fact_value = facts.find('span',class_='ds-body ds-home-fact-value').text
        facts_dict[fact_label] = fact_value
    return facts_dict

def get_interior_details(pageSoup):
    interior_details = []
    main_details = pageSoup.find('div',class_='ds-home-facts-and-features reso-facts-features sheety-facts-features')
    features_soup = main_details.find_all('div',recursive=False)[0].find_all('div',recursive=False)[1]

    details_soup = features_soup.find_all('div',recursive=False)[0]
    ## We are at interior Details

    bold_heading_groups = details_soup.find_all('div',recursive=False)[0].find_all('div',recursive=False)
    for each_heading in bold_heading_groups:
        heading = each_heading.find('span').text
        list_items = each_heading.find_all('li')
        list_items_list = []
        for list_item in list_items:
            list_items_list.append(list_item.text)
        interior_details.append(Tuple(heading,list_items_list).__dict__)
    return interior_details




def get_property_details(pageSoup):
    property_details = []
    main_details = pageSoup.find('div',class_='ds-home-facts-and-features reso-facts-features sheety-facts-features')
    features_soup = main_details.find_all('div',recursive=False)[0].find_all('div',recursive=False)[1]

    details_soup = features_soup.find_all('div',recursive=False)[1]
    ## We are at Property Details

    bold_heading_groups = details_soup.find_all('div',recursive=False)[0].find_all('div',recursive=False)
    for each_heading in bold_heading_groups:
        heading = each_heading.find('span').text
        list_items = each_heading.find_all('li')
        list_items_list = []
        for list_item in list_items:
            list_items_list.append(list_item.text)
        property_details.append(Tuple(heading,list_items_list).__dict__)
    return property_details


def get_construction_details(pageSoup):
    construction_details = []
    main_details = pageSoup.find('div', class_='ds-home-facts-and-features reso-facts-features sheety-facts-features')
    features_soup = main_details.find_all('div', recursive=False)[0].find_all('div', recursive=False)[1]

    details_soup = features_soup.find_all('div', recursive=False)[2]
    ## We are at Construction Details

    bold_heading_groups = details_soup.find_all('div', recursive=False)[0].find_all('div', recursive=False)
    for each_heading in bold_heading_groups:
        heading = each_heading.find('span').text
        list_items = each_heading.find_all('li')
        list_items_list = []
        for list_item in list_items:
            list_items_list.append(list_item.text)
        construction_details.append(Tuple(heading, list_items_list).__dict__)
    return construction_details

def get_utility_and_energy_details(pageSoup):
    utility_and_energy_details = []
    main_details = pageSoup.find('div', class_='ds-home-facts-and-features reso-facts-features sheety-facts-features')
    features_soup = main_details.find_all('div', recursive=False)[0].find_all('div', recursive=False)[1]

    details_soup = features_soup.find_all('div', recursive=False)[3]
    ## We are at Construction Details

    bold_heading_groups = details_soup.find_all('div', recursive=False)[0].find_all('div', recursive=False)
    for each_heading in bold_heading_groups:
        heading = each_heading.find('span').text
        list_items = each_heading.find_all('li')
        list_items_list = []
        for list_item in list_items:
            list_items_list.append(list_item.text)
        utility_and_energy_details.append(Tuple(heading, list_items_list).__dict__)
    return utility_and_energy_details




def get_community_and_neighbourhood_details(pageSoup):
    community_and_neighbourhood_details = []
    main_details = pageSoup.find('div', class_='ds-home-facts-and-features reso-facts-features sheety-facts-features')
    features_soup = main_details.find_all('div', recursive=False)[0].find_all('div', recursive=False)[1]

    details_soup = features_soup.find_all('div', recursive=False)[4]
    ## We are at Construction Details

    bold_heading_groups = details_soup.find_all('div', recursive=False)[0].find_all('div', recursive=False)
    for each_heading in bold_heading_groups:
        heading = each_heading.find('span').text
        list_items = each_heading.find_all('li')
        list_items_list = []
        for list_item in list_items:
            list_items_list.append(list_item.text)
        community_and_neighbourhood_details.append(Tuple(heading, list_items_list).__dict__)
    return community_and_neighbourhood_details

def get_hoa_and_financial_details(pageSoup):
    hoa_and_financial_details = []
    main_details = pageSoup.find('div', class_='ds-home-facts-and-features reso-facts-features sheety-facts-features')
    features_soup = main_details.find_all('div', recursive=False)[0].find_all('div', recursive=False)[1]

    details_soup = features_soup.find_all('div', recursive=False)[5]
    ## We are at Construction Details

    bold_heading_groups = details_soup.find_all('div', recursive=False)[0].find_all('div', recursive=False)
    for each_heading in bold_heading_groups:
        heading = each_heading.find('span').text
        list_items = each_heading.find_all('li')
        list_items_list = []
        for list_item in list_items:
            list_items_list.append(list_item.text)
        hoa_and_financial_details.append(Tuple(heading, list_items_list).__dict__)
    return hoa_and_financial_details

def get_other_details(pageSoup):
    other_details = []
    main_details = pageSoup.find('div', class_='ds-home-facts-and-features reso-facts-features sheety-facts-features')
    features_soup = main_details.find_all('div', recursive=False)[0].find_all('div', recursive=False)[1]

    details_soup = features_soup.find_all('div', recursive=False)[6]
    ## We are at Construction Details

    bold_heading_groups = details_soup.find_all('div', recursive=False)[0].find_all('div', recursive=False)
    for each_heading in bold_heading_groups:
        heading = each_heading.find('span').text
        list_items = each_heading.find_all('li')
        list_items_list = []
        for list_item in list_items:
            list_items_list.append(list_item.text)
        other_details.append(Tuple(heading, list_items_list).__dict__)
    return other_details

def record_xml_util(property_page_soup):
    record_xml = dict()
    summary = get_summary(pageSoup=property_page_soup)
    record_xml["summary"] = summary
    record_xml['address'] = get_address(property_page_soup)
    record_xml['overview'] = get_overview(property_page_soup)
    # listed_agent = get_listing_agent(pageSoup=property_page_soup)
    record_xml["listing_agent"] = get_listing_agent(property_page_soup)
    facts = get_facts(property_page_soup)
    record_xml['facts'] = facts
    interior_details = get_interior_details(property_page_soup)
    record_xml['interior_details'] = interior_details
    record_xml['property_details'] = get_property_details(property_page_soup)
    record_xml['construction_details'] = get_construction_details(property_page_soup)
    record_xml['utility_and_energy_details'] = get_utility_and_energy_details(property_page_soup)
    record_xml['community_and_neibhourhood_details'] = get_community_and_neighbourhood_details(property_page_soup)
    record_xml['hoa_and_financial_details'] = get_hoa_and_financial_details(property_page_soup)
    record_xml['other_details'] = get_other_details(property_page_soup)
    my_item_func = lambda x: x[:-1]
    xml = dicttoxml.dicttoxml(record_xml, attr_type=False, custom_root='home', item_func = my_item_func)
    dom = parseString(xml)
    return dom


def record(property_link,save_xml=True):
    headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36'}
    req = Request(property_link, headers=headers)
    page = urlopen(req).read()
    parts_of_url = property_link.split("/")
    parts_of_url = parts_of_url[len(parts_of_url) - 2].split("_")
    property_page_soup = BeautifulSoup(page, 'html.parser')
    dom = record_xml_util(property_page_soup)
    if save_xml:
        if not os.path.exists('zillow_xml'):
            os.makedirs('zillow_xml')
        output_path = "zillow_xml/" + str(parts_of_url[0]) + ".xml"
        f = open(output_path, "w")
        f.write(dom.toprettyxml())
        f.close()
    if not os.path.exists('zillow_html'):
        os.makedirs('zillow_html')
    output_path = "zillow_html/" + str(parts_of_url[0]) + ".html"
    with open(output_path, "w") as k:
        k.write(str(property_page_soup))