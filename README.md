# 🗺️ 2D Spatial Data Structures & File Organization

A project developed for the **Data Structures and Files** course at the **School of Electrical and Computer Engineering, Technical University of Crete**. 

This application implements and evaluates custom data structures designed to manage two-dimensional coordinates (x,y), simulating geographic map data. It contrasts the performance of in-memory execution against direct binary disk file manipulation.

---

## 🚀 Features & Architecture

### 🧠 Main Memory Execution (Part A)
* **Linked Lists:** Custom list implementation utilizing head and tail pointers for immediate insertions without traversal.
* **Hash Table:** An array of size M containing linked list buckets.
* **Hash Function:** Points are mapped using the formula `H(x,y) = (x*N + y) % M`.
* **Metrics:** Search algorithms calculate and return the exact number of memory comparisons required to find a target coordinate.

### 💾 Disk-Based Storage & Processing (Part B)
* **Binary File I/O:** Coordinates are saved strictly as 8-byte binary pairs, stripped of formatting characters like commas or parentheses.
* **Custom Disk Paging:** Data is managed strictly in fixed 256-byte disk pages using an active memory buffer.
* **Disk Linked Lists & Overflow:** When a 256-byte page reaches capacity, the system automatically generates overflow pages at the end of the file, chaining them together via stored addresses.
* **I/O Metrics:** Instead of memory comparisons, disk search efficiency is measured by the total number of physical `read` and `write` disk accesses required.

### 📊 Performance Evaluation (Part C)
* **Scalability Testing:** Structures are benchmarked using dataset sizes (K) of 1k, 10k, 30k, 50k, 70k, and 100k points on a grid where N = 65536.
* **Analytics:** Evaluates the average cost of 100 successful and 100 unsuccessful searches for both memory algorithms and disk accesses.

---

## 💻 Technical Details
* **Environment:** Cross-platform compilation supported for Linux and Windows.
* **Core Mechanisms:** Direct file pointer manipulation using low-level `seek`, `read`, and `write` commands.
* **Documentation:** Accompanied by a detailed technical report analyzing performance curves, justifying algorithmic efficiency, and providing compilation instructions.
