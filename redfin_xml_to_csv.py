from os import listdir
from os.path import isfile, join
import xml.etree.ElementTree as ET
import csv
import os


main_stats_fieldnames = ['listing_id',
                         'price',
                         'beds',
                         'baths',
                         'area',
                         'cost_per_squre_feet']


address_fieldnames = ['listing_id',
                      'street_address',
                      'locality',
                      'region',
                      'postal_code']


price_insights_fieldnames = ['listing_id',
                          'listing_price',
                          'est._mo._payment',
                          'redfin_estimate',
                          'price_per_Sq_ft']


home_facts_fieldnames = ['listing_id',
                    'property_type',
                    'hoa_dues',
                    'style',
                    'year_built',
                    'community',
                    'MLS#']


public_facts_fieldnames = [ 'listing_id',
                 'beds',
                 'baths',
                 'finished_sq_ft',
                 'unfinished_sq_ft',
                 'total_sq.ft',
                 'stories',
                 'lot_size',
                 'style',
                 'year_built',
                 'year_renovated',
                 'county',
                 'APN' ]


agents_fieldnames = ['agent_id',
                  'agent_name',
                  'agent_license',
                  'agent_org']


listed_by_fieldnames = ['agent_id',
             'listing_id',
             'status']

def setup():
    with open('redfin_csv/main_stats.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=main_stats_fieldnames)
        writer.writeheader()

    with open('redfin_csv/address.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=address_fieldnames)
        writer.writeheader()

    with open('redfin_csv/price_insights.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=price_insights_fieldnames)
        writer.writeheader()

    with open('redfin_csv/home_facts.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=home_facts_fieldnames)
        writer.writeheader()

    with open('redfin_csv/public_facts.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=public_facts_fieldnames)
        writer.writeheader()

    with open('redfin_csv/agents.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=agents_fieldnames)
        writer.writeheader()

    with open('redfin_csv/listed_by.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=listed_by_fieldnames)
        writer.writeheader()

def create_main_stats(root,house_id):
    dicti=dict()
    dicti['listing_id'] = house_id
    print(house_id)
    dicti['price'] = root.find('main_stats').find('Price')
    if(dicti['price'] is None):
        dicti['price'] = root.find('main_stats').find('Listed_at_Price')
    dicti['price'] = dicti['price'].text
    dicti['beds'] = root.find('main_stats').find('Beds')
    if dicti['beds'] is None:
        dicti['beds'] = root.find('main_stats').find('Bed')
    dicti['beds'] = dicti['beds'].text
    dicti['baths'] = root.find('main_stats').find('Baths')
    if dicti['baths'] is None:
        dicti['baths'] = root.find('main_stats').find('Bath')
    dicti['baths'] = dicti['baths'].text
    dicti['area'] = root.find('main_stats').find('key')
    if (dicti['area'] is None ):
            dicti['area'] = ""
    else:
        dicti['area'] = dicti['area'].text
    dicti['cost_per_squre_feet'] = root.find('main_stats').find('key')
    if(dicti['cost_per_squre_feet'] is None):
        dicti['cost_per_squre_feet'] = ""
    else:
        dicti['cost_per_squre_feet'] = dicti['cost_per_squre_feet'].attrib['name']
    with open('redfin_csv/main_stats.csv', 'a', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=main_stats_fieldnames)
        writer.writerow(dicti)



def create_address_table(root,house_id):
    street_address = root.find('HomeInfo_Address').find('street-address').text
    locality = root.find('HomeInfo_Address').find('locality').text
    region = root.find('HomeInfo_Address').find('region').text
    postal_code =root.find('HomeInfo_Address').find('postal-code').text
    with open('redfin_csv/address.csv', 'a', newline='') as csvFile:
        addressWriter = csv.DictWriter(csvFile, fieldnames=address_fieldnames)
        addressWriter.writerow({'listing_id':house_id,
                            'street_address': street_address,
                            'locality': locality,
                            'region': region,
                            'postal_code': postal_code})

def create_price_insights(root,house_id):
    dicti = dict()
    dicti['listing_id'] = house_id
    dicti['listing_price'] = root.find('price_insights').find('List_Price')
    if (dicti['listing_price'] is None):
        dicti['listing_price'] = ""
    else:
        dicti['listing_price'] = dicti['listing_price'].text
    dicti['est._mo._payment'] = root.find('price_insights').find('Est._Mo._Payment').text
    dicti['redfin_estimate'] = root.find('price_insights').find('Redfin_Estimate')
    if (dicti['redfin_estimate'] is None ):
        dicti['redfin_estimate'] = ""
    else:
        dicti['redfin_estimate'] = dicti['redfin_estimate'].text

    dicti['price_per_Sq_ft'] = root.find('price_insights').find('key')
    if (dicti['price_per_Sq_ft'] is None):
        dicti['price_per_Sq_ft'] = ""
    else:
        dicti['price_per_Sq_ft'] = dicti['price_per_Sq_ft'].text

    with open('redfin_csv/price_insights.csv', 'a', newline='') as csvFile:
        writers = csv.DictWriter(csvFile, fieldnames=price_insights_fieldnames)
        writers.writerow(dicti)

def create_home_facts(root,house_id):
    dicti = dict()
    dicti['listing_id'] = house_id

    dicti['property_type'] = root.find('home_facts').find('Property_Type')
    if (dicti['property_type'] is None):
        dicti['property_type'] = ""
    else:
        dicti['property_type'] = dicti['property_type'].text

    dicti['hoa_dues'] = root.find('home_facts').find('HOA_Dues')
    if (dicti['hoa_dues'] is None):
        dicti['hoa_dues'] = ""
    else:
        dicti['hoa_dues'] = dicti['hoa_dues'].text

    dicti['year_built'] = root.find('home_facts').find('Year_Built')
    if (dicti['year_built'] is None):
        dicti['year_built'] = ""
    else:
        dicti['year_built'] = dicti['year_built'].text

    dicti['style'] = root.find('home_facts').find('Style')
    if (dicti['style'] is None):
        dicti['style'] = ""
    else:
        dicti['style'] = dicti['style'].text

    dicti['community'] = root.find('home_facts').find('Community')
    if (dicti['community'] is None):
        dicti['community'] = ""
    else:
        dicti['community'] = dicti['community'].text

    dicti['MLS#'] = root.find('price_insights').find('key')
    if (dicti['MLS#'] is None):
        dicti['MLS#'] = ""
    else:
        dicti['MLS#'] = dicti['MLS#'].text

    with open('redfin_csv/home_facts.csv', 'a', newline='') as csvFile:
        writers = csv.DictWriter(csvFile, fieldnames=home_facts_fieldnames)
        writers.writerow(dicti)


def create_public_facts(root,house_id):
    dicti = dict()
    dicti['listing_id'] = house_id

    dicti['beds'] = root.find('public_facts').find('Beds')
    if (dicti['beds'] is None):
        dicti['beds'] = ""
    else:
        dicti['beds'] = dicti['beds'].text

    dicti['baths'] = root.find('public_facts').find('Baths')
    if (dicti['baths'] is None):
        dicti['baths'] = ""
    else:
        dicti['baths'] = dicti['baths'].text

    dicti['finished_sq_ft'] = root.find('public_facts').find('Finished_Sq._Ft.')
    if (dicti['finished_sq_ft'] is None):
        dicti['finished_sq_ft'] = ""
    else:
        dicti['finished_sq_ft'] = dicti['finished_sq_ft'].text

    dicti['unfinished_sq_ft'] = root.find('public_facts').find('Unfinished_Sq._Ft.')
    if (dicti['unfinished_sq_ft'] is None):
        dicti['unfinished_sq_ft'] = ""
    else:
        dicti['unfinished_sq_ft'] = dicti['unfinished_sq_ft'].text

    dicti['total_sq.ft'] = root.find('public_facts').find('Total_Sq._Ft.')
    if (dicti['total_sq.ft'] is None):
        dicti['total_sq.ft'] = ""
    else:
        dicti['total_sq.ft'] = dicti['total_sq.ft'].text

    dicti['year_renovated'] = root.find('public_facts').find('Year_Renovated')
    if (dicti['year_renovated'] is None):
        dicti['year_renovated'] = ""
    else:
        dicti['year_renovated'] = dicti['year_renovated'].text

    dicti['style'] = root.find('public_facts').find('Style')
    if (dicti['style'] is None):
        dicti['style'] = ""
    else:
        dicti['style'] = dicti['style'].text

    dicti['lot_size'] = root.find('public_facts').find('Lot_Size')
    if (dicti['lot_size'] is None):
        dicti['lot_size'] = ""
    else:
        dicti['lot_size'] = dicti['lot_size'].text

    dicti['stories'] = root.find('public_facts').find('Stories')
    if (dicti['stories'] is None):
        dicti['stories'] = ""
    else:
        dicti['stories'] = dicti['stories'].text

    dicti['year_built'] = root.find('public_facts').find('Year_Built')
    if (dicti['year_built'] is None):
        dicti['year_built'] = ""
    else:
        dicti['year_built'] = dicti['year_built'].text

    dicti['county'] = root.find('public_facts').find('County')
    if (dicti['county'] is None):
        dicti['county'] = ""
    else:
        dicti['county'] = dicti['county'].text

    dicti['APN'] = root.find('public_facts').find('APN')
    if (dicti['APN'] is None):
        dicti['APN'] = ""
    else:
        dicti['APN'] = dicti['APN'].text



    with open('redfin_csv/public_facts.csv', 'a', newline='') as csvFile:
        writers = csv.DictWriter(csvFile, fieldnames=public_facts_fieldnames)
        writers.writerow(dicti)

def create_listed_by(root,house_id):

    agentName = root.find('listed_by').find('name')
    if (agentName is None):
        agentName = ""
    else:
        agentName = agentName.text

    agentLicenseNumber = root.find('listed_by').find('agentLicenseDisplay')
    if (agentLicenseNumber is None):
        agentLicenseNumber = ""
    else:
        agentLicenseNumber = agentLicenseNumber.text

    org = root.find('listed_by').find('org')
    if (org is None):
        org = ""
    else:
        org = org.text

    status = root.find('home_facts').find('Status')
    if (org is None):
        status = ""
    else:
        status = status.text
    found = find_in_listing_agent(listing_agents, agentName, agentLicenseNumber, org)

    with open('redfin_csv/listed_by.csv', 'a', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=listed_by_fieldnames)
        agent_house_dict = dict()
        agent_house_dict['listing_id'] = house_id
        agent_house_dict['agent_id'] = found
        agent_house_dict['status'] = status
        writer.writerow(agent_house_dict)


if not os.path.exists('redfin_csv'):
    os.makedirs('redfin_csv')
path_to_xml_data='redfin_xml_from_html/'
xml_files = [(f,join(path_to_xml_data, f)) for f in listdir(path_to_xml_data) if isfile(join(path_to_xml_data, f))]
if ('.DS_Store', 'redfin_xml_from_html/.DS_Store') in xml_files:
    xml_files.remove(('.DS_Store','redfin_xml_from_html/.DS_Store'))
setup()


def find_in_listing_agent(listing_agents,agentName,agentLicenseNumber,org_name):
    found_index = next((index for (index, d) in enumerate(listing_agents) if
                        d["agent_name"] == agentName
                        and d["agent_license"] == agentLicenseNumber
                        and d["agent_org"] == org_name)
                       , None)
    if found_index is None:
        return False

    return listing_agents[found_index]['agent_id']

def create_listing_agents():
    listing_agents = []
    count = 0

    for (file, xml_file) in xml_files:
        print(file)
        tree = ET.parse(xml_file)
        root = tree.getroot()

        agentName = root.find('listed_by').find('name')
        if (agentName is None):
            agentName = ""
        else:
            agentName = agentName.text

        agentLicenseNumber = root.find('listed_by').find('agentLicenseDisplay')
        if (agentLicenseNumber is None):
            agentLicenseNumber = ""
        else:
            agentLicenseNumber = agentLicenseNumber.text

        org = root.find('listed_by').find('org')
        if (org is None):
            org = ""
        else:
            org = org.text

        found = find_in_listing_agent(listing_agents,agentName,agentLicenseNumber,org)
        if(not found):
            listing_agent_dict = dict()
            listing_agent_dict['agent_id'] = (count+1)
            listing_agent_dict['agent_name'] = agentName
            listing_agent_dict['agent_license'] = agentLicenseNumber
            listing_agent_dict['agent_org'] = org

            with open('redfin_csv/agents.csv', 'a', newline='') as csvFile:
                csvWriter = csv.DictWriter(csvFile, fieldnames=listing_agent_dict)
                csvWriter.writerow(listing_agent_dict)

            listing_agents.append(listing_agent_dict)
            count = count+1

    return listing_agents

listing_agents = create_listing_agents()
for (file,xml_file) in xml_files:
    house_id = file.split('.')[0];
    tree = ET.parse(xml_file)
    root = tree.getroot()
    create_main_stats(root,house_id)
    create_address_table(root,house_id)
    create_price_insights(root,house_id)
    create_home_facts(root,house_id)
    create_public_facts(root,house_id)
    create_listed_by(root,house_id)

