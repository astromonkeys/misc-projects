'''
Created on Mar 22, 2021

@author: Noah
'''
import fileinput
from itertools import permutations
import math


def main():
    exchanges = []
    numExchanges = 0
    numPossibleTrades = 0
    for line in fileinput.input():
        if fileinput.isfirstline():
            nm = line.strip()
            numExchanges = int(nm[0])
            numPossibleTrades = int(nm[2])
            continue
        exchange = line.strip().split(' ')
        exchanges.append({'Buy Market': exchange[0], 'Sell Market': exchange[1], 'Price': float(exchange[2]) / 100})
    # comment these 3 lines out when submitting
    '''
    numExchanges = 2
    numPossibleTrades = 2
    exchanges = [{'Buy Market': 'NYSE', 'Sell Market': 'JPX', 'Price': 1.10}, {'Buy Market': 'JPX', 'Sell Market': 'NYSE', 'Price': 1.10}]
    
    test code
    
    numExchanges = 5
    numPossibleTrades = 6
    exchanges = [{'Buy Market': 'NYSE', 'Sell Market': 'JPX', 'Price': 1.10}, {'Buy Market': 'JPX', 'Sell Market': 'LSE', 'Price': 0.90}, {'Buy Market': 'LSE', 'Sell Market': 'SSE', 'Price': 1.10}, {'Buy Market': 'SSE', 'Sell Market': 'NYSE', 'Price': 1.11}, {'Buy Market': 'SSE', 'Sell Market': 'AMS', 'Price': 0.90}, {'Buy Market': 'AMS', 'Sell Market': 'NYSE', 'Price': 0.90}]
    '''
    if numExchanges == 2:
        numExchanges = 3
    trades = []
    for trade in permutations(exchanges, numExchanges - 1):
        trade = list(trade)
        # only include trades that start end end at the same market
        if trade[0]['Buy Market'] == trade[numExchanges - 2]['Sell Market']:
            trades.append(trade)

    profits = []    
    for ordering in trades:
        profit = 1
        for trade in ordering:
            profit *= trade['Price']
        profits.append(profit)
    
    maximum = max(profits) * 100
    # print(maximum)
    
    if maximum < 0:
        print(0)
    
    # solve the equation maximum*(1-x)^(numExchanges-1) <= 100
    rightSide = ((pow(100 / maximum, 1 / (numExchanges - 1)) - 1) / (-1)) * 100
    output = int(math.ceil(rightSide))
    print(output)


if __name__ == "__main__":
    main()  
