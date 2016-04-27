from __future__ import division
import os

pattern = 'Gatekeeper'
clients = 100
run = 5

total_seconds = 805

total_consumption = 0

for a_file in os.listdir('%s/%dclients/run%d' %(pattern,clients,run)):
    if a_file.endswith('.txt'):
        with open('%s/%dclients/run%d/%s' %(pattern,clients,run,a_file), 'r') as f:
            reader = f.read().split('\n')
            i = 0
            for line in reader:
                if len(line) and i < total_seconds:
                    power_elems = line.split(';')[-1]
                    total_consumption += float(power_elems.split('=')[-1])
                    i += 1

print 'Total consumption:'
print total_consumption