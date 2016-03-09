from hex.genmodel.easy import RowData
from hex.genmodel.easy import EasyPredictModelWrapper
from java.lang import Math
import MaliciousDomainModel
from collections import Counter

f = open('../var/task/words.txt', 'r')
words = set(f.read().splitlines())


def predict(domain):
  row = RowData()
  row.put('length', float(len(domain)))
  row.put('entropy', entropy(domain))
  row.put('p_vowels', p_vowels(domain))
  row.put('p_substrings_words', p_substrings_words(domain))

  model = EasyPredictModelWrapper(MaliciousDomainModel())
  p = model.predictBinomial(row)
  return [float(p.label), p.classProbabilities[0], p.classProbabilities[1]]


# Shannon entropy
def entropy(domain):
  sume = 0
  chars = Counter(domain)
  N = float(len(domain))
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
  return sumv / float(len(domain))


# proportion of all substrings >= 2 characters that are valid english words
def p_substrings_words(domain):
  word_count = 0
  total_substrings = len(domain) * (len(domain) - 1) / 2
  for i in range(len(domain) - 1):
    for j in range(i + 2, len(domain) + 1):
      if domain[i:j] in words:
        word_count += 1
  return word_count / float(total_substrings)
