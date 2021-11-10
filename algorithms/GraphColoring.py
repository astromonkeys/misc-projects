'''
Created on Apr 26, 2021

@author: Noah Zurn nzurn@wisc.edu
'''
import sys
import numpy as np
import os


def main():
    stdin_read = sys.stdin
    '''
4 6 3
1 2
1 3
1 4
2 3
2 4
3 4
exit
    '''
    ''' read input '''
    U, V = [], []
    n, m, k = 0, 0, 0
    for line in stdin_read:
        str1 = line.strip()
        if str1 == 'exit':
            break  # for debugging only
        else:
            str1 = str1.split()
        if str1.__len__() == 3:
            n = int(str1[0])
            m = int(str1[1])
            k = int(str1[2])
        else:
            U.append(int(str1[0]))
            V.append(int(str1[1]))
    ''' build matrix, n rows(vertices) and m columns(edges) '''
    incidence = [[0 for i in range(m)] for j in range(n)]
    i = 0
    for vertex1, vertex2 in zip(U, V):
        incidence[vertex1 - 1][i], incidence[vertex2 - 1][i] = 1, 1
        i += 1
    ''' assign clauses '''
    Fv, Fe = [], []
    for u in range(n):
        vic = []
        for i in range(k):
            num = int(str(u + 1) + str(i))
            vic.append(num)
        vic.append(0)
        Fv.append(vic)
    for u, v in zip(U, V):
        for i in range(k):
            num1 = int('-' + str(u) + str(i))
            num2 = int('-' + str(v) + str(i))
            ei = [num1, num2, 0]
            Fe.append(ei)
            
    checked = []
    num_vars = 0
    for array in Fv:
        for i in range(array.__len__()):
            if array[i] not in checked and array[i] != 0:
                checked.append(array[i])
                num_vars += 1
    for array in Fe:
        for i in range(array.__len__()):
            if array[i] not in checked and array[i] != 0:
                checked.append(array[i])
                num_vars += 1
    num_clauses = Fv.__len__() + Fe.__len__()
    firstline = "p cnf " + str(num_vars) + " " + str(num_clauses)
    
    with open("input.txt", "w") as file:
        file.truncate(0)
        file.write(firstline)
        file.write("\n")
        separator = " "
        for array in Fv:
            file.write(separator.join(map(str, array)))
            file.write("\n")
        for array in Fe:
            file.write(separator.join(map(str, array)))    
            file.write("\n")
        file.close()
    cmd = "minisat input.txt output.txt >/dev/null 2>&1"
    os.system(cmd)
    with open("output.txt") as fo:
        result = fo.readline().rstrip()
       
    if result == "UNSAT":
        print(False)
    else:
        print(True)

    
if __name__ == "__main__":
    main()
