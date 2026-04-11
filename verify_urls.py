import urllib.request

urls = [
    "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400&q=80",
    "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=400&q=80",
    "https://images.unsplash.com/photo-1576045057995-568f588f82cb?w=400&q=80",
    "https://images.unsplash.com/photo-1574484284002-952d92456975?w=400&q=80",
    "https://images.unsplash.com/photo-1488459716781-31db52582fe9?w=400&q=80"
]

for url in urls:
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        response = urllib.request.urlopen(req)
        print(f"OK: {url}")
    except Exception as e:
        print(f"Error ({e}): {url}")
