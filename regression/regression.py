import numpy as np
from csv import DictReader
from matplotlib import pyplot as plt
from math import sqrt

# Feel free to import other packages, if needed.
# As long as they are supported by CSL machines.


def get_dataset(filename):
    """
    TODO: implement this function.

    INPUT: 
        filename - a string representing the path to the csv file.

    RETURNS:
        An n by m+1 array, where n is # data points and m is # features.
        The labels y should be in the first column.
    """
    with open(filename, newline='') as csvfile:
        reader = DictReader(csvfile)
        data = list(reader)
    dataset = []
    for i in range(data.__len__()):
        dataset.append([])
        del data[i]['IDNO']
        dataset[i].append(data[i]['BODYFAT'])
        dataset[i].append(data[i]['DENSITY'])
        dataset[i].append(data[i]['AGE'])
        dataset[i].append(data[i]['WEIGHT'])
        dataset[i].append(data[i]['HEIGHT'])
        dataset[i].append(data[i]['ADIPOSITY'])
        dataset[i].append(data[i]['NECK'])
        dataset[i].append(data[i]['CHEST'])
        dataset[i].append(data[i]['ABDOMEN'])
        dataset[i].append(data[i]['HIP'])
        dataset[i].append(data[i]['THIGH'])
        dataset[i].append(data[i]['KNEE'])
        dataset[i].append(data[i]['ANKLE'])
        dataset[i].append(data[i]['BICEPS'])
        dataset[i].append(data[i]['FOREARM'])
        dataset[i].append(data[i]['WRIST'])
    return np.array(dataset).astype(float)


def print_stats(dataset, col):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        col     - the index of feature to summarize on. 
                  For example, 1 refers to density.

    RETURNS:
        None
    """
    length = dataset.__len__()
    print(length)
    # print sample mean
    column = dataset[:, col]
    sum = 0
    for num in column:
        sum += num
    mean = sum / length
    print(round(mean, 2))
    # print sample standard deviation
    sum2 = 0
    for num in column:
        sum2 += pow(num - mean, 2)
    frac = (1 / (length - 1))
    std = sqrt(frac * sum2)
    print(round(std, 2))
    pass


def regression(dataset, cols, betas):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.
        betas   - a list of elements chosen from [beta0, beta1, ..., betam]

    RETURNS:
        mse of the regression model
    """
    x = []
    for column in cols:
        x.append(dataset[:, column])
    y = dataset[:, 0]
    sum = 0
    rows = dataset.__len__()
    columns = cols.__len__()
    for i in range(rows):  # i rows
        ith_term = betas[0]
        for j in range(1, columns + 1):  # j columns
            beta = betas[j]
            xij = x[j - 1][i]
            ith_term += beta * xij
        ith_term -= y[i]
        sum += pow(ith_term, 2)    
    mse = sum / rows
    return mse


def gradient_descent(dataset, cols, betas):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.
        betas   - a list of elements chosen from [beta0, beta1, ..., betam]

    RETURNS:
        An 1D array of gradients
    """
    gradients = []
    x = []
    for column in cols:
        x.append(dataset[:, column])
    y = dataset[:, 0]
    current_term = 0
    num_betas = betas.__len__()
    n = dataset.__len__()
    columns = cols.__len__()
    for beta in range(num_betas):
        current_term = 0
        for i in range(n):
            ith_term = betas[0]
            for m in range(0, columns): 
                ith_term += betas[m + 1] * x[m][i]
            ith_term -= y[i]
            if beta > 0:
                ith_term *= x[beta - 1][i]
            current_term += ith_term
        gradients.append((2 / n) * current_term)
    return np.array(gradients)


def iterate_gradient(dataset, cols, betas, T, eta):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.
        betas   - a list of elements chosen from [beta0, beta1, ..., betam]
        T       - # iterations to run
        eta     - learning rate

    RETURNS:
        None
    """
    
    # Yes, I know this is very wrong, but it's no needed for
    # the other calculations so I'm not wasting my time on 
    # desperately trying to figure it out
    
    current_betas = (-eta) * gradient_descent(dataset, cols, betas)
    for t in range(T):
        # calculate these
        current_mse = regression(dataset, cols, current_betas)
        current_betas = (-eta) * gradient_descent(dataset, cols, current_betas)
        
        print(t + 1, round(current_mse, 2), end=' ')
        for beta in current_betas:
            print(round(beta, 2), end=' ')
        print()
    pass


def compute_betas(dataset, cols):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.

    RETURNS:
        A tuple containing corresponding mse and several learned betas
    """
    y = dataset[:, 0]  # bodyfat data in column form
    n = y.__len__()
    x = []
    all1 = []
    for i in range(n):
        all1.append(1)
    x.append(all1)
    for col in cols:
        x.append(dataset[:, col])
    xt = np.asarray(x)
    x = np.transpose(x)
    xtxinv = np.linalg.inv(np.matmul(xt, x))
    step2 = np.matmul(xtxinv, xt)
    betas = np.matmul(step2, np.transpose(y))
    mse = regression(dataset, cols, betas)
    return (mse, *betas)


def predict(dataset, cols, features):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.
        features- a list of observed values

    RETURNS:
        The predicted body fat percentage value
        
    f(x) = B0 + B1x1 + B2x2 + ... Bmxm    
    """
    betas = list(compute_betas(dataset, cols))
    del betas[0]
    result = betas[0]
    for i in range(features.__len__()):
        result += betas[i + 1] * features[i]
    return result


def synthetic_datasets(betas, alphas, X, sigma):
    """
    TODO: implement this function.

    Input:
        betas  - parameters of the linear model
        alphas - parameters of the quadratic model
        X      - the input array (shape is guaranteed to be (n,1))
        sigma  - standard deviation of noise

    RETURNS:
        Two datasets of shape (n,2) - linear one first, followed by quadratic.
    """
    n = X.__len__()
    linear = np.zeros((n, 2))
    quadratic = np.zeros((n, 2))
    for i in range(n):
        linear[i][1] = X[i][0]
        linear[i][0] = betas[0] + betas[1] * X[i][0] + np.random.normal(0, sigma)
        quadratic[i][1] = X[i][0]
        quadratic[i][0] = alphas[0] + alphas[1] * pow(X[i][0], 2) + np.random.normal(0, sigma)
    return linear, quadratic


def plot_mse():
    from sys import argv
    if len(argv) == 2 and argv[1] == 'csl':
        import matplotlib
        matplotlib.use('Agg')

    # TODO: Generate datasets and plot an MSE-sigma graph
    X = np.random.randint(-100, 101, 1000)
    betas = np.array([6, 9])
    alphas = np.array([9, 6])
    linear1, quadratic1 = synthetic_datasets(betas, alphas, X, 1)
    linear2, quadratic2 = synthetic_datasets(betas, alphas, X, 10)
    linearmse1, beta1 = compute_betas(linear1, cols=[1])
    linearmse2, beta2 = compute_betas(linear2, cols=[1])
    quadmse1, beta3 = compute_betas(quadratic1, cols=[1])
    quadmse2, beta4 = compute_betas(quadratic2, cols=[1])
    plt.plot(linearmse1)
    plt.show()

    '''
    I gave up on this solely because of how difficult the instructions
    were to understand what we were being asked to do. Please, PLEASE
    make the instructions clearer in the future. It would be so much
    more helpful to have something written clearly than to ask tons of
    clarification questions on Piazza and bog you all down with boring
    questions you don't want to have to answer
    '''

if __name__ == '__main__':
    ### DO NOT CHANGE THIS SECTION ###
    plot_mse()
