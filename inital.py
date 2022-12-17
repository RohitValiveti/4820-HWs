from collections import deque
import sys

maxVal = 999999999


def main():
    lines = [i.strip() for i in sys.stdin.readlines()]
    friends, values, colors = [int(i) for i in lines[0].split()]
    lines = lines[1:]

    for friend in range(friends):
        line = lines[friend].split()
        cards = [
            [int(line[j]) - 1, int(line[j + 1]) - 1]
            for j in range(0, len(line)-1, 2)]
        for cardVal, cardColor in cards:
            print("Friend {fr} has card with value {value}, color {color}".format(fr=friend + 1, value=cardVal + 1, color=cardColor + 1))

    num_vertices = 4
    srcIndex = 0
    sinkIndex = 3
    num_edges = 5
    # these are hard-coded into lists only because it's easier to read off the original graph.
    # you'll probably want to generate the lists on the fly in your implementation without intermediate lists.
    edge_heads = [0, 0, 2, 1, 2]
    edge_tails = [1, 2, 1, 3, 3]
    edge_caps = [1, 100, 1, 100, 1]

    capacities = []
    dests = [[] for _ in range(num_vertices)]
    ids = [[] for _ in range(num_vertices)]

    edge_index = 0

    for i in range(num_edges):
        u = edge_heads[i]
        v = edge_tails[i]
        c = edge_caps[i]

        dests[u].append(v)
        dests[v].append(u)
        ids[u].append(edge_index)
        edge_index += 1
        ids[v].append(edge_index)
        edge_index += 1
        capacities.append(c)
        capacities.append(0)

    backtrack = [0 for _ in range(len(ids) * 2)]
    minima = [0 for _ in range(len(ids))]

    def bfs():
        visited = [False for _ in range(len(ids))]
        queue = deque()
        queue.append(srcIndex)
        backtrack[srcIndex] = -maxVal
        backtrack[2 * srcIndex + 1] = -1
        visited[srcIndex] = True
        minima[srcIndex] = maxVal

        while queue:
            currentNode = queue.popleft()
            currMin = minima[currentNode]
            for i in range(len(dests[currentNode])):
                neighbor = dests[currentNode][i]
                edge = ids[currentNode][i]
                if not visited[neighbor] and capacities[edge] > 0:
                    newMin = min(currMin, capacities[edge])
                    backtrack[2 * neighbor] = currentNode
                    backtrack[2 * neighbor + 1] = edge
                    minima[neighbor] = newMin
                    if neighbor == sinkIndex:
                        return True
                    queue.append(neighbor)
                    visited[neighbor] = True
        return False



    def ff():
        maxFlow = 0
        while (bfs()):
            currCap = minima[sinkIndex]
            edgeEnd = sinkIndex
            while edgeEnd != srcIndex:
                edge = backtrack[2 * edgeEnd + 1]
                rev_edge = edge - 1 if edge % 2 else edge + 1
                capacities[edge] -= currCap
                capacities[rev_edge] += currCap
                edgeEnd = backtrack[2 * edgeEnd]
            maxFlow += currCap
        return maxFlow

    print(ff())

main()