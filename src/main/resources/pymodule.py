from __future__ import division
from hex.genmodel.easy import RowData
from hex.genmodel.easy import EasyPredictModelWrapper
from java.lang import Math
import MaliciousDomainModel
import MaliciousDomainModel.BETA
import NamesHolder_MaliciousDomainModel
from collections import Counter

f = open('../var/task/words.txt', 'r')
words = set(f.read().splitlines())

def predict(domain):
  domain = domain.split('.')[0]
  row = RowData()
  functions = [len, entropy, p_vowels, num_valid_substrings]
  eval_features = [f(domain) for f in functions]
  names = NamesHolder_MaliciousDomainModel().VALUES
  beta = MaliciousDomainModel().BETA().VALUES
  feature_coef_product = [beta[len(beta) - 1]]
  for i in range(len(names)):
    row.put(names[i], float(eval_features[i]))
    feature_coef_product.append(eval_features[i] * beta[i])

  #prediction
  model = EasyPredictModelWrapper(MaliciousDomainModel())
  p = model.predictBinomial(row)

  #[label, class0Prob, class1Prob], [intercept], [features] = 3 + 1 + x
  return [float(p.label), p.classProbabilities[0], p.classProbabilities[1]] + feature_coef_product 


# Shannon entropy
def entropy(domain):
  sume = 0
  chars = Counter(domain)
  N = len(domain)
  for n in chars.values():
    sume += - n / N * Math.log(n / N) / Math.log(2)
  return sume


# proportion of vowels
def p_vowels(domain):
  sumv = 0
  chars = Counter(domain)
  vowels = 'aeiou'
  for v in vowels:
    sumv += chars[v]
  return sumv / len(domain)


# num of all substrings >= 2 characters that are valid english words
def num_valid_substrings(domain):
  word_count = 0
  for i in range(len(domain) - 1):
    for j in range(i + 2, len(domain) + 1):
      if domain[i:j] in words:
        word_count += 1
  return word_count
