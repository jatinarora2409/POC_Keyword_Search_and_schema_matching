from os import listdir
from os.path import isfile, join
import xml.etree.ElementTree as ET
import csv
import os

overview_fieldnames = ['house_id',
                       'overview']

address_fieldnames = ['house_id',
                      'street_address',
                      'city',
                      'state',
                      'zipcode']

homefacts_fieldnames = ['house_id',
                        'beds',
                        'baths',
                        'living_area',
                        'house_type',
                        'year_built',
                        'heating',
                        'cooling',
                        'parking',
                        'price',
                        'price_per_sqft']

interior_fieldnames = ['house_id',
                       'full_bathrooms',
                       'half_bathrooms',
                       'basement',
                       'flooring',
                       'appliances',
                       'total_interior_livable_area',
                       'fireplace']


property_fieldnames = ['house_id',
                       'parking_features',
                       'garage_spaces',
                       'total_spaces',
                       'covered_spaces',
                       'stories',
                       'exterior_features',
                       'on_waterfront',
                       'lot_size',
                       'parcel_number']

construction_details_fieldnames = ['house_id',
                                    'home_type',
                                    'roof_material',
                                    'new_construction',
                                    'major_remodel_year']

utilities_and_energy_fieldnames = ['house_id',
                                'sun_score',
                                'rent_control']

HOA_and_financial_fieldnames = ['house_id',
                                'tax_assessed_value',
                                'annual_tax_amount']

meta_details_fieldnames = ['house_id',
                           'mls_id']


listing_agent_fieldnames = ['agent_id',
                            'agent_name',
                            'phone_number',
                            'agent_license_number',
                            'posting_group_name',
                            'posting_website_link_text']

agent_house_status_fieldnames = ['house_id',
                      'agent_id']

def setup():

    with open('zillow_csv/overview.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=overview_fieldnames)
        writer.writeheader()

    with open('zillow_csv/address.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=address_fieldnames)
        writer.writeheader()

    with open('zillow_csv/home_facts.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=homefacts_fieldnames)
        writer.writeheader()

    with open('zillow_csv/interior_details.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=interior_fieldnames)
        writer.writeheader()

    with open('zillow_csv/property_details.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=property_fieldnames)
        writer.writeheader()

    with open('zillow_csv/construction_details.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=construction_details_fieldnames)
        writer.writeheader()

    with open('zillow_csv/utility_and_energy_details.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=utilities_and_energy_fieldnames)
        writer.writeheader()

    with open('zillow_csv/hoa_and_financial_details.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=HOA_and_financial_fieldnames)
        writer.writeheader()

    with open('zillow_csv/metadata_details.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=meta_details_fieldnames)
        writer.writeheader()

    with open('zillow_csv/listing_agents.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=listing_agent_fieldnames)
        writer.writeheader()

    with open('zillow_csv/agent_house_listing.csv', 'w', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=agent_house_status_fieldnames)
        writer.writeheader()




def create_metadata_details(root,house_id):
    meta_det = root.find('other_details')
    items = meta_det.findall('other_detail')
    meta_det = dict()
    meta_det['house_id'] = house_id

    for item in items:
        item_2 = item.findall('details')
        my_item = item_2[0]
        items_in_my_item = my_item.findall('detail')
        for my_item_final in items_in_my_item:
            text = my_item_final.text
            value = text.split(":")
            if(len(value)==2):
                value = text.split(":")[1].strip()
                if 'MLS ID' in text:
                    meta_det['mls_id'] = value

    with open('zillow_csv/metadata_details.csv', 'a', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=meta_details_fieldnames)
        writer.writerow(meta_det)

def create_hoa_and_financial_details(root,house_id):
    hoa_det = root.find('hoa_and_financial_details')
    if(hoa_det is None):
        print(house_id)
    items = hoa_det.findall('hoa_and_financial_detail')
    hoa_dict = dict()
    hoa_dict['house_id'] = house_id

    for item in items:
        item_2 = item.findall('details')
        my_item = item_2[0]
        items_in_my_item = my_item.findall('detail')
        for my_item_final in items_in_my_item:
            text = my_item_final.text
            value = text.split(":")[1].strip()
            if 'Tax assessed value' in text:
                hoa_dict['tax_assessed_value'] = value
            if 'Annual tax amount' in text:
                hoa_dict['annual_tax_amount'] = value

    with open('zillow_csv/hoa_and_financial_details.csv', 'a', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=HOA_and_financial_fieldnames)
        writer.writerow(hoa_dict)



def create_utlity_and_energy_details(root,house_id):
    ue_det = root.find('utility_and_energy_details')
    items = ue_det.findall('utility_and_energy_detail')
    ue_dict = dict()
    ue_dict['house_id'] = house_id

    for item in items:
        item_2 = item.findall('details')
        my_item = item_2[0]
        items_in_my_item = my_item.findall('detail')
        for my_item_final in items_in_my_item:
            text = my_item_final.text
            value = text.split(":")[1].strip()
            if 'Sunscore' in text:
                ue_dict['sun_score'] = value
            if 'Rent control' in text:
                ue_dict['rent_control'] = value

    with open('zillow_csv/utility_and_energy_details.csv', 'a', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=utilities_and_energy_fieldnames)
        writer.writerow(ue_dict)


def create_construction_details(root,house_id):
    cons_de = root.find('construction_details')
    items = cons_de.findall('construction_detail')
    cons_dict = dict()
    cons_dict['house_id'] = house_id

    for item in items:
        item_2 = item.findall('details')
        my_item = item_2[0]
        items_in_my_item = my_item.findall('detail')
        for my_item_final in items_in_my_item:
            text = my_item_final.text
            value = text.split(":")[1].strip()
            if 'Home type:' in text:
                cons_dict['home_type'] = value
            if 'Roof' in text:
                cons_dict[ 'roof_material'] = value
            if 'New construction' in text:
                cons_dict['new_construction'] = value
            if 'Major remodel year:' in text:
                cons_dict['major_remodel_year'] = value

    with open('zillow_csv/construction_details.csv', 'a', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=construction_details_fieldnames)
        writer.writerow(cons_dict)


def create_property_details(root,house_id):
    pro_de = root.find('property_details')
    items = pro_de.findall('property_detail')
    pro_dict = dict()
    pro_dict['house_id'] = house_id
    for item in items:
        item_2 = item.findall('details')
        my_item = item_2[0]
        items_in_my_item = my_item.findall('detail')
        for my_item_final in items_in_my_item:
            text = my_item_final.text
            value = text.split(":")[1].strip()
            if 'Parking features' in text:
                pro_dict['parking_features'] = value
            if 'Garage spaces' in text:
                pro_dict[ 'garage_spaces'] = value
            if 'Total spaces' in text:
                pro_dict['total_spaces'] = value
            if 'Covered spaces' in text:
                pro_dict['covered_spaces'] = value
            if 'Stories' in text:
                pro_dict['stories'] = value
            if 'Exterior Features' in text:
                pro_dict['exterior_features'] = value
            if 'Waterfront' in text:
                pro_dict['on_waterfront'] = value
            if 'Lot size' in text:
                pro_dict['lot_size'] = value
            if 'Parcel number' in text:
                pro_dict['parcel_number'] = value
    with open('zillow_csv/property_details.csv', 'a', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=property_fieldnames)
        writer.writerow(pro_dict)


def create_interior_details(root,house_id):
    int_de = root.find('interior_details')
    items = int_de.findall('interior_detail')
    int_dict = dict()
    int_dict['house_id'] = house_id
    for item in items:
        item_2 = item.findall('details')
        my_item = item_2[0]
        items_in_my_item = my_item.findall('detail')
        for my_item_final in items_in_my_item:
            text = my_item_final.text
            value = text.split(":")[1].strip()
            if 'Full bathrooms' in text:
                int_dict['full_bathrooms'] = value
            if 'Half bathrooms' in text:
                int_dict[ 'half_bathrooms'] = value
            if 'Basement' in text:
                int_dict['basement'] = value
            if 'Flooring' in text:
                int_dict['flooring'] = value
            if 'Appliances' in text:
                int_dict['appliances'] = value
            if 'livable area' in text:
                int_dict['total_interior_livable_area'] = value
            if 'Fireplace' in text:
                int_dict['fireplace'] = value

    with open('zillow_csv/interior_details.csv', 'a', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=interior_fieldnames)
        writer.writerow(int_dict)


def create_overview(root,house_id):
    overview_text = root.find('overview').text
    with open('zillow_csv/overview.csv', 'a', newline='') as csvFile:
        csvWriter = csv.DictWriter(csvFile,fieldnames=overview_fieldnames)
        csvWriter.writerow({'house_id': house_id, 'overview':overview_text})

def create_Address_Table(root,house_id):
    street_address = root.find('address').find('street_address').text.split(",")[0]
    city_state = root.find('address').find('city_state')
    city = city_state.text.split(',')[0][1:len(city_state.text.split(',')[0])]
    state = city_state.text.split(',')[1].strip().split(' ')[0]
    zipcode = city_state.text.split(',')[1].strip().split(' ')[1]
    with open('zillow_csv/address.csv', 'a', newline='') as csvFile:
        addressWriter = csv.DictWriter(csvFile, fieldnames=address_fieldnames)
        addressWriter.writerow({'house_id':house_id,
                            'street_address': street_address,
                            'city': city,
                            'state': state,
                            'zipcode': zipcode})


def create_home_facts(root,house_id):
    facts = root.find('facts')
    facts = facts.findall('key')
    facts_dict = dict()
    facts_dict['house_id']=house_id
    facts_dict['beds'] = root.find('summary').find('bd').text
    facts_dict['baths'] = root.find('summary').find('ba').text
    facts_dict['living_area'] = root.find('summary').find('area').text
    facts_dict['price'] = root.find('summary').find('price').text

    for child in facts:
        if child.attrib['name'] == 'Type:':
            facts_dict['house_type'] = child.text
        if child.attrib['name'] == 'Year built:':
            facts_dict['year_built'] = child.text
        if child.attrib['name'] == 'Heating:':
            facts_dict['heating'] = child.text
        if child.attrib['name'] == 'Cooling:':
            facts_dict['cooling'] =  child.text
        if child.attrib['name'] == 'Parking:':
            facts_dict['parking'] = child.text
        if child.attrib['name'] == 'Price/sqft:':
            facts_dict['price_per_sqft'] = child.text

    with open('zillow_csv/home_facts.csv', 'a', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=homefacts_fieldnames)
        writer.writerow(facts_dict)






def find_in_listing_agent(listing_agents,agentName,phoneNumber,agentLicenseNumber,postingGroupName):
    found_index = next((index for (index, d) in enumerate(listing_agents) if (
                        d["agent_name"] == agentName)
                        and d["phone_number"] == phoneNumber
                        and d["agent_license_number"] == agentLicenseNumber
                        and d["posting_group_name"] == postingGroupName)
                       , None)
    if found_index is None:
        return False

    return listing_agents[found_index]['agent_id']

def create_listing_agents_house_id(root,house_id,listing_agents):
    agentName = root.find('listing_agent').find('agentName').text
    phoneNumber = root.find('listing_agent').find('phoneNumber').text
    agentLicenseNumber = root.find('listing_agent').find('agentLicenseNumber').text
    postingGroupName = root.find('listing_agent').find('postingGroupName').text
    found = find_in_listing_agent(listing_agents, agentName, phoneNumber, agentLicenseNumber, postingGroupName)
    with open('zillow_csv/agent_house_listing.csv', 'a', newline='') as csvFile:
        writer = csv.DictWriter(csvFile, fieldnames=agent_house_status_fieldnames)
        agent_house_dict = dict()
        agent_house_dict['house_id'] = house_id
        agent_house_dict['agent_id'] = found
        writer.writerow(agent_house_dict)



def create_listing_agents():
    listing_agents = []
    count = 0

    for (file, xml_file) in xml_files:
        print(file)
        tree = ET.parse(xml_file)
        root = tree.getroot()
        agentName = root.find('listing_agent').find('agentName').text
        phoneNumber = root.find('listing_agent').find('phoneNumber').text
        agentLicenseNumber = root.find('listing_agent').find('agentLicenseNumber').text
        postingGroupName = root.find('listing_agent').find('postingGroupName').text
        postingWebsiteLinkText = root.find('listing_agent').find('postingWebsiteLinkText').text
        found = find_in_listing_agent(listing_agents,agentName,phoneNumber,agentLicenseNumber,postingGroupName)
        if(not found):
            listing_agent_dict = dict()
            listing_agent_dict['agent_id'] = (count+1)
            listing_agent_dict['agent_name'] = agentName
            listing_agent_dict['phone_number'] = phoneNumber
            listing_agent_dict['agent_license_number'] =agentLicenseNumber
            listing_agent_dict['posting_group_name'] = postingGroupName
            listing_agent_dict['posting_website_link_text'] =postingWebsiteLinkText

            with open('zillow_csv/listing_agents.csv', 'a', newline='') as csvFile:
                csvWriter = csv.DictWriter(csvFile, fieldnames=listing_agent_fieldnames)
                csvWriter.writerow(listing_agent_dict)

            listing_agents.append(listing_agent_dict)
            count = count+1

    return listing_agents
            # <agentName>Greg Gaddy</agentName>
        # 		<phoneNumber>202-333-1212</phoneNumber>
        # 		<agentLicenseNumber/>
        # 		<postingGroupName>TTR Sotheby's International Realty</postingGroupName>
        # 		<postingWebsiteLinkText>TTR Sotheby's International Realty</postingWebsiteLinkText>


if not os.path.exists('zillow_csv'):
    os.makedirs('zillow_csv')
path_to_xml_data='zillow_xml_from_html/'
test = True
xml_files = [(f,join(path_to_xml_data, f)) for f in listdir(path_to_xml_data) if isfile(join(path_to_xml_data, f))]
if ('.DS_Store', 'zillow_xml_from_html/.DS_Store') in xml_files:
    xml_files.remove(('.DS_Store','zillow_xml_from_html/.DS_Store'))
if(test==False):
    file = xml_files[0][0]
    xml_file = xml_files[0][1]
    house_id = file.split('.')[0];
    tree = ET.parse(xml_file)
    root = tree.getroot()
    create_construction_details(root,house_id)
    exit()
setup()
listing_agents = create_listing_agents()
for (file,xml_file) in xml_files:
    house_id = file.split('.')[0];
    tree = ET.parse(xml_file)
    root = tree.getroot()
    create_overview(root,house_id)
    create_Address_Table(root,house_id)
    create_home_facts(root,house_id)
    create_interior_details(root,house_id)
    create_property_details(root,house_id)
    create_construction_details(root,house_id)
    create_utlity_and_energy_details(root,house_id)
    create_listing_agents_house_id(root,house_id,listing_agents)
    ## TODO: FIX THIS
    create_hoa_and_financial_details(root,house_id)
    create_metadata_details(root,house_id)

