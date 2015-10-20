import json
from pprint import pprint

with open('qa.json') as input:
    data = json.load(input)

hits = data["hits"]["hits"]

vardgivare = {}
for record in hits:
    source = record["_source"]
    enhet = source["unit"]
    vg = enhet[0:enhet.find('-')]
    vg_dict = vardgivare.get(vg, {})
    vg_dict[enhet] = vg_dict.get(enhet, 0) + 1
    vardgivare[vg] = vg_dict

print("Landsting, antal")
for key, value in vardgivare.items():
    print('{}, {}'.format(key, sum(value.values())))
print("Totalt, {}".format(sum([sum(vg.values()) for vg in vardgivare.values()] )))

