# A-subgraph-matching-algorithm-based-on-subgraph-index-for-knowledge-graph

The problem of subgraph matching is one fundamental issue in graph search, which is NP-Complete problem. Recently, subgraph matching has become a popular research topic in the field of knowledge graph analysis, which has a wide range of applications including question answering and semantic search. In this paper, we study the problem of subgraph matching on knowledge graph. Specifically, given a query graph $q$ and a data graph $G$, the problem of subgraph matching is to conduct all possible subgraph isomorphic mappings of $q$ on $G$. Knowledge graph is formed as a directed labeled multi-graph having multiple edges between a pair of vertices and it has more dense semantic and structural features than general graph. To accelerating subgraph matching on knowledge graph, we propose a novel subgraph matching algorithm based on subgraph index for knowledge graph, called as $FGq_T$-Match. The subgraph matching algorithm consists of two key designs. One design is a subgraph index of matching-driven flow graph ($FGq_T$), which reduces redundant calculations in advance. Another design is a multi-label weight matrix, which evaluates a near-optimal matching tree for minimizing the intermediate candidates. With the aid of these two key designs, all subgraph isomorphic mappings are quickly conducted only by traversing $FGq_T$. Extensive empirical studies on real and synthetic graphs demonstrate that our techniques outperform the state-of-the-art algorithms.

We upload a scribbled version in advance, and make further revisions in the future.
