#!/usr/bin/env python3
import csv
import os
import sys

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt

csv_path = sys.argv[1] if len(sys.argv) > 1 else "results/results.csv"
out_dir = sys.argv[2] if len(sys.argv) > 2 else "results/plots"
os.makedirs(out_dir, exist_ok=True)

rows = []
with open(csv_path, newline="") as f:
    for r in csv.DictReader(f):
        rows.append({
            "workload": r["workload"], "variant": r["variant"], "structure": r["structure"],
            "n": int(r["n"]), "time_ms": float(r["time_ms"]),
            "steps": int(r["steps"]), "moves": int(r["moves"]), "comparisons": int(r["comparisons"]),
        })

TITLES = {
    "W1": "W1 - Random access (10 000 get)",
    "W2": "W2 - Search (1 000 contains, half present)",
    "W3": "W3 - Insert & remove (1 000 + 1 000)",
    "W4": "W4 - Priority processing (n insert + n extractMin)",
}
STRUCT_STYLE = {"DynamicArray": "-", "MyLinkedList": "--", "MinHeap": "-"}
STRUCT_COLOR = {"DynamicArray": "tab:blue", "MyLinkedList": "tab:red", "MinHeap": "tab:green"}
METRIC_COLOR = {"steps": "tab:blue", "moves": "tab:orange", "comparisons": "tab:green"}
METRIC_MARKER = {"steps": "o", "moves": "s", "comparisons": "^"}


def series(workload, variant, structure, key):
    pts = sorted((r["n"], r[key]) for r in rows
                 if r["workload"] == workload and r["variant"] == variant and r["structure"] == structure)
    return [p[0] for p in pts], [p[1] for p in pts]


def finish(ax, ylabel, title):
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (number of elements)")
    ax.set_ylabel(ylabel)
    ax.set_title(title)
    ax.grid(True, which="both", alpha=0.3)
    ax.legend(fontsize=8)


for w in sorted({r["workload"] for r in rows}):
    variants = sorted({r["variant"] for r in rows if r["workload"] == w})
    structures = sorted({r["structure"] for r in rows if r["workload"] == w})

    fig, axes = plt.subplots(1, len(variants), figsize=(6 * len(variants), 4.5), squeeze=False)
    for ax, v in zip(axes[0], variants):
        for s in structures:
            xs, ys = series(w, v, s, "time_ms")
            ax.plot(xs, ys, STRUCT_STYLE[s], color=STRUCT_COLOR[s], marker="o", label=s)
        finish(ax, "Median time (ms)", TITLES[w] + ("" if v == "-" else f" - {v}"))
    fig.tight_layout()
    fig.savefig(os.path.join(out_dir, f"{w}_time.png"), dpi=150)
    plt.close(fig)

    # --- счётчики ---
    fig, axes = plt.subplots(1, len(variants), figsize=(6 * len(variants), 4.5), squeeze=False)
    for ax, v in zip(axes[0], variants):
        for s in structures:
            for key in ("steps", "moves", "comparisons"):
                xs, ys = series(w, v, s, key)
                pts = [(x, y) for x, y in zip(xs, ys) if y > 0]
                if not pts:
                    continue
                ax.plot([p[0] for p in pts], [p[1] for p in pts], STRUCT_STYLE[s],
                        color=METRIC_COLOR[key], marker=METRIC_MARKER[key], label=f"{s}: {key}")
        finish(ax, "Operations (count)", TITLES[w] + ("" if v == "-" else f" - {v}"))
    fig.tight_layout()
    fig.savefig(os.path.join(out_dir, f"{w}_counters.png"), dpi=150)
    plt.close(fig)

print("plots saved to", out_dir)