/*
URL shortener: encodes long URLs into short base62 codes and decodes them
back, with collision-safe storage (a counter-based code plus a reverse map
so the same long URL always maps back to its one short code).

status - completed
*/

import { logp } from "../utils/logger";

const BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

function encodeBase62(num: number): string {
  if (num === 0) return BASE62_ALPHABET[0];
  let result = "";
  let n = num;
  while (n > 0) {
    result = BASE62_ALPHABET[n % 62] + result;
    n = Math.floor(n / 62);
  }
  return result;
}

class UrlShortener {
  private readonly longToCode: Map<string, string> = new Map();
  private readonly codeToLong: Map<string, string> = new Map();
  private nextId = 1;

  constructor(private readonly baseUrl: string = "https://short.ly/") {}

  shorten(longUrl: string): string {
    const existingCode = this.longToCode.get(longUrl);
    if (existingCode) {
      return this.baseUrl + existingCode;
    }

    let code: string;
    do {
      code = encodeBase62(this.nextId++);
    } while (this.codeToLong.has(code)); // guards against any future non-monotonic id source

    this.longToCode.set(longUrl, code);
    this.codeToLong.set(code, longUrl);
    return this.baseUrl + code;
  }

  expand(shortUrl: string): string | undefined {
    const code = shortUrl.startsWith(this.baseUrl) ? shortUrl.slice(this.baseUrl.length) : shortUrl;
    return this.codeToLong.get(code);
  }
}

// usage scenarios

const shortener = new UrlShortener();

const short1 = shortener.shorten("https://example.com/some/very/long/path?query=1");
const short2 = shortener.shorten("https://example.com/another/long/path");
logp(`shortened url 1 -> ${short1}`);
logp(`shortened url 2 -> ${short2}`);

const short1Again = shortener.shorten("https://example.com/some/very/long/path?query=1");
logp(`shortening the same long url again returns the same code -> ${short1Again === short1}`);

logp(`expand(short1) -> ${shortener.expand(short1)}`);
logp(`expand(short2) -> ${shortener.expand(short2)}`);
logp(`expand of unknown code -> ${shortener.expand("https://short.ly/ZZZZZ")} (expected undefined)`);

const many: string[] = [];
for (let i = 0; i < 5; i++) {
  many.push(shortener.shorten(`https://example.com/page-${i}`));
}
const allUnique = new Set(many).size === many.length;
logp(`5 distinct urls produced ${many.length} codes, all unique: ${allUnique} -> ${many.join(", ")}`);
