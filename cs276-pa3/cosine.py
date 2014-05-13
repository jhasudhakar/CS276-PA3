#!/usr/bin/python
from subprocess import Popen
from subprocess import PIPE
import numpy as np

CONFIG = 'cosine.config'

cosine_config = {
    'fieldWeights#body': 0.2,
    'fieldWeights#anchor': 2.0,
    'fieldWeights#title': 1.0,
    'fieldWeights#url': 1.0,
    'fieldWeights#header': 1.0,
}

commands = ["./run.sh", "train", "cosine"]

def func(x, *params):
    cosine_config['fieldWeights#url'] = x[0]
    cosine_config['fieldWeights#title'] = x[1]
    cosine_config['fieldWeights#header'] = x[2]
    write_dict_to_file(cosine_config, CONFIG)
    p = Popen(commands, stdout=PIPE)
    output = float(p.communicate()[0].strip())
    print "config:", cosine_config
    print "ndcg:", output
    print
    return -output

def write_dict_to_file(dictionary, filename):
    """Write a dictionary to file
    """
    with open(filename, 'w') as f:
        for key in dictionary:
            f.write(key + '=' + str(dictionary[key]) + '\n')

if __name__ == '__main__':
    write_dict_to_file(cosine_config, 'cosine.config')
    from scipy.optimize import brute, fmin
    rranges = (slice(0.9, 2, 0.3), slice(0.8, 2.1, 0.3), slice(0, 1, 0.2))
    res = brute(func, rranges, full_output=True, finish=fmin)
    print res[0]
    print res[1]
