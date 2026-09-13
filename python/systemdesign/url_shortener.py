"""
URL shortener that encodes long URLs into short base62 codes and
decodes them back, staying collision-safe by keying off an
incrementing counter rather than hashing the URL (so two encodes of
the same URL round-trip through the same stored mapping).

status - completed
"""

from base_logger.logging_event import create_logger

ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
BASE = len(ALPHABET)


class UrlShortener:

    log = create_logger(__name__)

    def __init__(self, base_url="https://short.ly/"):
        self.base_url = base_url
        self.url_to_code = {}
        self.code_to_url = {}
        self._next_id = 1

    def _encode_id(self, num: int) -> str:
        if num == 0:
            return ALPHABET[0]
        digits = []
        while num > 0:
            num, remainder = divmod(num, BASE)
            digits.append(ALPHABET[remainder])
        return "".join(reversed(digits))

    def shorten(self, long_url: str) -> str:
        if long_url in self.url_to_code:
            code = self.url_to_code[long_url]
        else:
            code = self._encode_id(self._next_id)
            self._next_id += 1
            self.url_to_code[long_url] = code
            self.code_to_url[code] = long_url
        return self.base_url + code

    def expand(self, short_url: str) -> str:
        code = short_url[len(self.base_url):]
        if code not in self.code_to_url:
            raise KeyError(f"unknown short code: {code}")
        return self.code_to_url[code]


if __name__ == "__main__":
    log = create_logger("url_shortener_demo")

    shortener = UrlShortener()

    short1 = shortener.shorten("https://example.com/very/long/path/about/system/design")
    short2 = shortener.shorten("https://example.com/another/long/article")
    log.info("shortened url 1 -> %s", short1)
    log.info("shortened url 2 -> %s", short2)
    log.info("codes are distinct: %s", short1 != short2)

    short1_again = shortener.shorten("https://example.com/very/long/path/about/system/design")
    log.info("re-shortening the same url -> %s (expected same code as before)", short1_again)
    log.info("collision-safe (no new code minted): %s", short1 == short1_again)

    expanded = shortener.expand(short1)
    log.info("expand(%s) -> %s", short1, expanded)

    try:
        shortener.expand("https://short.ly/doesnotexist")
    except KeyError as exc:
        log.info("expanding unknown code raised: %s", exc)
