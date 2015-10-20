import json
from pprint import pprint

with open('login.json') as input:
    data = json.load(input)

doctors = data["facets"]["terms"]["terms"]

count = {}
uniq = {}
for doctor in doctors:
    hsa = doctor["term"]
    vg = hsa[0:hsa.find('-')]
    count[vg] = count.get(vg,0) + doctor["count"]
    doctors_set = uniq.get(vg, set())
    doctors_set.add(hsa)
    uniq[vg] = doctors_set

print('vårdgivare, total, unika')
for vg in count:
    print('{}, {}, {}'.format(vg, count[vg], len(uniq[vg])))

print('{}, {}, {}'.format("totalt", sum(count.values()), sum(map(lambda x: len(x), uniq.values()))))

