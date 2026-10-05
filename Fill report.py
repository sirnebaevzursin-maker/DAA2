#!/usr/bin/env python3

import csv
import math
import sys

csv_path = sys.argv[1] if len(sys.argv) > 1 else "results/results.csv"
tpl_path = sys.argv[2] if len(sys.argv) > 2 else "REPORT.template.md"
out_path = sys.argv[3] if len(sys.argv) > 3 else "REPORT.md"

rows = []
with open(csv_path, newline="") as f:
    for r in csv.DictReader(f):
        rows.append({"w": r["workload"], "v": r["variant"], "s": r["structure"], "n": int(r["n"]),
                     "t": float(r["time_ms"]), "steps": int(r["steps"]),
                     "moves": int(r["moves"]), "cmp": int(r["comparisons"])})
N = max(r["n"] for r in rows)


def get(w, v, s):
    for r in rows:
        if r["w"] == w and r["v"] == v and r["s"] == s and r["n"] == N:
            return r
    raise SystemExit(f"no row for {w},{v},{s},n={N} in {csv_path}")


def fmt_int(x):
    return f"{x:,}".replace(",", " ")


def fmt_t(x):
    return f"{x:.2f}" if x >= 0.01 else f"{x:.4f}"


def ratio(a, b):
    return f"{a / max(b, 1e-9):.1f}"


def counters(r):
    return f"{fmt_int(r['steps'])} / {fmt_int(r['moves'])} / {fmt_int(r['cmp'])}"


A, L = "DynamicArray", "MyLinkedList"
vals = {}
for key, (w, v) in {"W1": ("W1", "-"), "W2": ("W2", "-"), "W3H": ("W3", "head"), "W3M": ("W3", "middle")}.items():
    a, l = get(w, v, A), get(w, v, L)
    vals[f"{key}_AT"], vals[f"{key}_LT"] = fmt_t(a["t"]), fmt_t(l["t"])
    vals[f"{key}_AC"], vals[f"{key}_LC"] = counters(a), counters(l)

w1a, w1l = get("W1", "-", A), get("W1", "-", L)
vals["W1_RATIO"] = ratio(w1l["t"], w1a["t"])

w2a, w2l = get("W2", "-", A), get("W2", "-", L)
vals["W2_CMP_A"], vals["W2_CMP_L"] = fmt_int(w2a["cmp"]), fmt_int(w2l["cmp"])
vals["W2_STEPS_A"], vals["W2_STEPS_L"] = fmt_int(w2a["steps"]), fmt_int(w2l["steps"])
vals["W2_RATIO"] = ratio(w2l["t"], w2a["t"])

h_a, h_l = get("W3", "head", A), get("W3", "head", L)
if h_l["t"] <= h_a["t"]:
    vals["W3H_SENTENCE"] = (
        f"In W3 at the head, the list does O(1) work per operation ({fmt_int(h_l['moves'])} moves and "
        f"{fmt_int(h_l['steps'])} steps in total), while the array shifts every element "
        f"({fmt_int(h_a['moves'])} moves), so the list is {ratio(h_a['t'], h_l['t'])}x faster "
        f"({fmt_t(h_l['t'])} ms vs {fmt_t(h_a['t'])} ms).")
else:
    vals["W3H_SENTENCE"] = (
        f"In W3 at the head, the list does O(1) work per operation ({fmt_int(h_l['moves'])} moves and "
        f"{fmt_int(h_l['steps'])} steps in total) against {fmt_int(h_a['moves'])} array moves, "
        f"but in this run the array was still {ratio(h_l['t'], h_a['t'])}x faster "
        f"({fmt_t(h_a['t'])} ms vs {fmt_t(h_l['t'])} ms), because the shifting is a fast sequential copy "
        f"and the list pays for allocating a node on every insertion.")

m_a, m_l = get("W3", "middle", A), get("W3", "middle", L)
if m_a["t"] <= m_l["t"]:
    vals["W3M_SENTENCE"] = (
        f"In W3 at the middle, both are Θ(n): the array makes {fmt_int(m_a['moves'])} moves and the list "
        f"{fmt_int(m_l['steps'])} steps, yet the array is {ratio(m_l['t'], m_a['t'])}x faster "
        f"({fmt_t(m_a['t'])} ms vs {fmt_t(m_l['t'])} ms), because shifting is a sequential, prefetch-friendly "
        f"copy while the walk to the middle is pointer chasing.")
else:
    vals["W3M_SENTENCE"] = (
        f"In W3 at the middle, both are Θ(n): the array makes {fmt_int(m_a['moves'])} moves and the list "
        f"{fmt_int(m_l['steps'])} steps, and in this run the list was {ratio(m_a['t'], m_l['t'])}x faster "
        f"({fmt_t(m_l['t'])} ms vs {fmt_t(m_a['t'])} ms); the array's shifts touch every element after the "
        f"insertion point on each operation, so equal Big-O does not decide the winner.")

hp = get("W4", "-", "MinHeap")
nlogn = N * math.log2(N)
vals["W4_TIME"], vals["W4_STEPS"] = fmt_t(hp["t"]), fmt_int(hp["steps"])
vals["W4_MOVES"], vals["W4_CMP"] = fmt_int(hp["moves"]), fmt_int(hp["cmp"])
vals["W4_NLOGN"] = fmt_int(round(nlogn))
vals["W4_RATIO"] = f"{hp['cmp'] / nlogn:.2f}"

text = open(tpl_path, encoding="utf-8").read()
for k, v in vals.items():
    text = text.replace("{{" + k + "}}", v)
if "{{" in text:
    raise SystemExit("unfilled placeholders left in template")
open(out_path, "w", encoding="utf-8").write(text)
print(f"{out_path} written from {csv_path} (n = {N})")