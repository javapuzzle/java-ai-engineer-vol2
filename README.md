<div align="center">

# ☕ Java AI Engineer — Volume 2: RAG & Knowledge Systems
### *Grounding Enterprise Data with High-Precision Retrieval*

[![Volume](https://img.shields.io/badge/Volume-2%20of%206-blue?style=for-the-badge)](https://javapuzzle.com)
[![Java](https://img.shields.io/badge/Java-21+-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-ai)
[![Vector DB](https://img.shields.io/badge/Pgvector%20%7C%20Qdrant-Active-blueviolet?style=for-the-badge)](https://javapuzzle.com)

**Part of the 6-volume [Java AI Engineer](https://javapuzzle.com) series by Java Puzzle**

</div>

---

## 📖 About This Volume

Sarah's AI assistant is live at Acme Retail, but customers keep asking questions that no SQL table or database row can answer: warranty terms, return policies, user manuals, and damaged product photos.

Across 20 technical chapters, Volume 2 systematically covers the entire lifecycle of Retrieval-Augmented Generation (RAG) in Java:
- **Vector Mechanics**: Embedding models, cosine vs dot product distance, indexing tradeoffs (HNSW, IVFFlat).
- **Ingestion & Processing**: Document parsing, semantically aware chunking, token boundary management.
- **Advanced Retrieval**: Hybrid Search (combining BM25 lexical + dense vectors), Query Expansion, and Cross-Encoder Reranking.
- **Enterprise Architectures**: Corrective RAG (CRAG), Adaptive RAG, GraphRAG, multi-tenant ACLs, and RAGAS metric evaluation.

---

## 🗺️ Chapter & Code Index

| Chapter | Title | Focus Area / Key Mechanism |
|:---:|---|---|
| **01** | `chapter-01` | **The Question the Database Can't Answer** — Why RAG exists; structured vs unstructured facts |
| **02** | `chapter-02` | **Turning Words Into Numbers** — High-dimensional embeddings, semantic vectors in Java |
| **03** | `chapter-03` | **Choosing an Embedding Model** — Dimension vs latency tradeoffs (text-embedding-3, bge-large) |
| **04** | `chapter-04` | **Vector Databases: Concepts and Tradeoffs** — ANN search algorithms: Flat, IVFFlat, and HNSW graphs |
| **05** | `chapter-05` | **Selecting the Right Vector DB for Acme Retail** — Evaluating Pgvector, Qdrant, Milvus, and embedded stores |
| **06** | `chapter-06` | **Document Loading and Preprocessing Pipelines** — Ingestion, metadata extraction, HTML & PDF parsing |
| **07** | `chapter-07` | **Chunking Strategies That Actually Work** — Fixed-size vs recursive character vs semantic chunking |
| **08** | `chapter-08` | **Building Acme Retail's First RAG Pipeline** — End-to-end Naive RAG in Spring AI |
| **09** | `chapter-09` | **Query Rewriting and Expansion** — Multi-query generation, HyDE (Hypothetical Document Embeddings) |
| **10** | `chapter-10` | **Reranking: Why Retrieval Isn't Enough** — Two-stage retrieval, Cross-Encoder scoring in Java |
| **11** | `chapter-11` | **Hybrid Search: Combining BM25 and Dense Retrieval** — Reciprocal Rank Fusion (RRF) for lexical + vector |
| **12** | `chapter-12` | **When One Retrieval Strategy Isn't Enough** — Routing, Corrective RAG (CRAG) & Adaptive pipelines |
| **13** | `chapter-13` | **Modular RAG Architectures** — Decoupled retrieval, formatting, post-processing, and fallback modules |
| **14** | `chapter-14` | **GraphRAG and Knowledge-Graph Retrieval** — Entity-relationship graphs combined with vector search |
| **15** | `chapter-15` | **Multilingual and Multimodal RAG** — Cross-lingual embeddings and image retrieval |
| **16** | `chapter-16` | **Reducing Hallucinations Through Prompting & Grounding** — Strict citation prompts, context bounding |
| **17** | `chapter-17` | **RAG Evaluation: Faithfulness, Relevance, Recall & RAGAS** — Automated LLM-as-a-Judge test suites |
| **18** | `chapter-18` | **Enterprise RAG: Access Control and Document Security** — Document-level ACLs, token-based filtering |
| **19** | `chapter-19` | **Monitoring and Optimizing RAG in Production** — Semantic caching, hit-ratio metrics, index drift tracking |
| **20** | `chapter-20` | **Acme Retail's Knowledge Assistant (Capstone)** — The complete enterprise RAG assistant |

---

## 🛠️ Tech Stack & Prerequisites

- **Java**: 21 LTS or newer
- **Framework**: Spring Boot 3.4+, Spring AI 1.0+
- **Vector DB**: Pgvector (PostgreSQL), Qdrant
- **Build Tool**: Maven 3.9+

## 🚀 Building & Running Chapter Code

```bash
mvn clean test
```

Step into any chapter to run tests:
```bash
cd chapter-08
mvn test
```

---

<div align="center">
<sub>© Java Puzzle. All rights reserved. Visit <a href="https://javapuzzle.com">javapuzzle.com</a> to explore the entire 6-volume series.</sub>
</div>
