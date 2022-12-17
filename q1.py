from collections import deque
import sys

maxVal = 999999999


def main():
    lines = [f.strip() for f in sys.stdin.readlines()]
    friends, values, colors = [int(f) for f in lines[0].split()]
    lines = lines[1:]

    edge_heads = []
    edge_tails = []
    edge_caps = []

    count = 2
    in_card = {(-1, -1, -1, 1): 0, (-2, -2, -2, 0): 1}
    out_card = {0: (-1, -1, -1, 1), 1: (-2, -2, -2, 0)}

    all_cards = [[[0 for _ in range(colors)]
              for _ in range(values)] for _ in range(friends)]

    for friend in range(friends):
        line = lines[friend].split()
        for v in range(0, len(line) - 1, 2):
            all_cards[friend][int(line[v]) - 1][int(line[v + 1]) - 1] += 1

    for f in range(friends):
        for v in range(values):
            for c in range(colors):
                if all_cards[f][v][c] > 0:
                    in_card[(f, v, c, 0)] = count
                    out_card[count] = (f, v, c, 0)
                    count += 1
                    in_card[(f, v, c, 1)] = count
                    out_card[count] = (f, v, c, 1)
                    count += 1

                    edge_heads.append(count - 2)
                    edge_tails.append(count - 1)
                    edge_caps.append(all_cards[f][v][c])

    for f in range(friends):
        for c in range(colors):
            if all_cards[f][0][c] > 0:
                edge_caps.append(maxVal)
                edge_heads.append(0)
                edge_tails.append(in_card[(f, 0, c, 0)])
            if all_cards[f][values - 1][c] > 0:
                edge_caps.append(maxVal)
                edge_heads.append(in_card[(f, values - 1, c, 1)])
                edge_tails.append(1)
    
    unique_n = set()

    for f in range(friends):
        for v in range(values):
            for c in range(colors):
                # Check all rules for playing card
                if all_cards[f][v][c] > 0:
                    src = in_card[(f, v, c, 1)]
                    i_prime = (f + 1) % friends

                    for c_prime in range(colors):
                      if c_prime != c and all_cards[i_prime][v][c_prime] > 0:
                          snk = in_card[(i_prime, v, c_prime, 0)]
                          if (src, snk) not in unique_n:
                            unique_n.add((src, snk))
                            unique_n.add((snk, src))
                            edge_caps.append(maxVal)
                            edge_heads.append(src)
                            edge_tails.append(snk)

                    if v + 1 < values and all_cards[i_prime][v + 1][c] > 0:
                        snk = in_card[(i_prime, v + 1, c, 0)]
                        if (src, snk) not in unique_n:
                            unique_n.add((src, snk))
                            unique_n.add((snk, src))
                            edge_caps.append(maxVal)
                            edge_heads.append(src)
                            edge_tails.append(snk)

                    if v + 1 < values and all_cards[f][v + 1][c] > 0:
                        snk = in_card[(f, v + 1, c, 0)]
                        if (src, snk) not in unique_n:
                            unique_n.add((src, snk))
                            unique_n.add((snk, src))
                            edge_caps.append(maxVal)
                            edge_heads.append(src)
                            edge_tails.append(snk)

                    for c_prime in range(colors):
                        if c_prime != c and all_cards[f][v][c_prime] > 0:
                            snk = in_card[(f, v, c_prime, 0)]
                            if (src, snk) not in unique_n:
                                unique_n.add((src, snk))
                                unique_n.add((snk, src))
                                edge_caps.append(maxVal)
                                edge_heads.append(src)
                                edge_tails.append(snk)
   

    num_vertices = len(in_card)
    srcIndex = 0
    sinkIndex = 1
    num_edges = len(edge_heads)

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
            for f in range(len(dests[currentNode])):
                neighbor = dests[currentNode][f]
                edge = ids[currentNode][f]
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
