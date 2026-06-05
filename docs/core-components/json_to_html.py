#!/usr/bin/env python3
import argparse
import csv
import html
import json
from pathlib import Path


def escape(value):
    return html.escape(str(value), quote=True)


def render_table(title, items, color_map):
    rows = []
    for item in items:
        urls = item.get("url") or []
        url_links = []
        for url in urls:
            safe = escape(url)
            url_links.append(f'<a href="{safe}">{safe}</a>')
        url_html = "<br>".join(url_links) if url_links else ""
        category = item.get("category", "")
        color = color_map.get(category)
        if color:
            category_html = f'<span class="cat" style="background:{escape(color)}">{escape(category)}</span>'
        else:
            category_html = escape(category)
        rows.append(
            "<tr>"
            f"<td>{escape(item.get('name', ''))}</td>"
            f"<td>{category_html}</td>"
            f"<td>{escape(item.get('layer', ''))}</td>"
            f"<td>{url_html}</td>"
            "</tr>"
        )

    return (
        f"<h2>{escape(title)}</h2>"
        "<table class=\"sortable\">"
        "<thead><tr><th>Name</th><th>Category</th><th>Layer</th><th>URL</th></tr></thead>"
        "<tbody>"
        + "".join(rows)
        + "</tbody></table>"
    )


def build_html(data):
    title = "Core RDK Components"
    categories = sorted({item.get("category", "") for item in data.get("components", []) if item.get("category")})
    color_map = {}
    if categories:
        step = 360 / max(len(categories), 1)
        for idx, cat in enumerate(categories):
            hue = int(idx * step)
            color_map[cat] = f"hsl({hue}, 70%, 85%)"
    head = (
        "<!doctype html>"
        "<html><head><meta charset=\"utf-8\">"
        f"<title>{escape(title)}</title>"
        "<style>"
        "body{font-family:Arial,Helvetica,sans-serif;margin:24px;}"
        "table{border-collapse:collapse;width:100%;margin:12px 0 24px;}"
        "th,td{border:1px solid #ccc;padding:6px 8px;vertical-align:top;}"
        "th{background:#f6f6f6;text-align:left;cursor:pointer;}"
        "th[data-sort-dir=\"asc\"]::after{content:\" ▲\";}"
        "th[data-sort-dir=\"desc\"]::after{content:\" ▼\";}"
        "h1,h2{margin:0 0 12px;}"
        "small{color:#555;}"
        ".cat{display:inline-block;padding:2px 6px;border-radius:10px;}"
        "</style></head><body>"
        f"<h1>{escape(title)}</h1>"
    )
    meta = (
        f"<p><small>Schema version: {escape(data.get('schemaVersion', ''))}"
        f" | Generated: {escape(data.get('generatedAt', ''))}</small></p>"
    )
    components_html = render_table("Core Components", data.get("components", []), color_map)
    parts = [head + meta, components_html]
    missing_items = data.get("missingFromCore") or []
    if missing_items:
        parts.append(render_table("Missing From Core", missing_items, color_map))
    parts.append(
        "<script>"
        "function getCellValue(row, idx){return row.children[idx].innerText.trim().toLowerCase();}"
        "function sortTable(table, idx, dir){"
        "const tbody=table.querySelector('tbody');"
        "const rows=Array.from(tbody.querySelectorAll('tr'));"
        "rows.sort((a,b)=>{const av=getCellValue(a,idx);const bv=getCellValue(b,idx);"
        "if(av<bv)return dir==='asc'?-1:1;"
        "if(av>bv)return dir==='asc'?1:-1;"
        "return 0;});"
        "rows.forEach(r=>tbody.appendChild(r));"
        "}"
        "document.addEventListener('DOMContentLoaded',()=>{"
        "document.querySelectorAll('table.sortable').forEach(table=>{"
        "table.querySelectorAll('th').forEach((th,idx)=>{"
        "th.addEventListener('click',()=>{"
        "const current=th.dataset.sortDir==='asc'?'desc':'asc';"
        "table.querySelectorAll('th').forEach(t=>delete t.dataset.sortDir);"
        "th.dataset.sortDir=current;"
        "sortTable(table,idx,current);"
        "});"
        "});"
        "});"
        "});"
        "</script>"
        "</body></html>"
    )
    return "".join(parts)


def write_csv(items, path: Path):
    with path.open("w", encoding="utf-8", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(["name", "category", "layer", "url"])
        for item in items:
            urls = item.get("url") or []
            writer.writerow(
                [
                    item.get("name", ""),
                    item.get("category", ""),
                    item.get("layer", ""),
                    "; ".join(urls),
                ]
            )


def main():
    parser = argparse.ArgumentParser(description="Convert core components JSON to HTML/CSV.")
    parser.add_argument("json_path", type=Path, help="Path to core-v-components.json")
    parser.add_argument("-o", "--output", type=Path, help="Output HTML file (defaults to stdout)")
    parser.add_argument("--csv", type=Path, help="Output CSV file")
    args = parser.parse_args()

    with args.json_path.open("r", encoding="utf-8") as f:
        data = json.load(f)

    if args.csv:
        write_csv(data.get("components", []), args.csv)

    html_text = build_html(data)
    if args.output:
        args.output.write_text(html_text, encoding="utf-8")
    elif not args.csv:
        print(html_text)


if __name__ == "__main__":
    main()
