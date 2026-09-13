# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repo is

A personal algorithm-practice monorepo, not a deployable application. It holds
the same categories of problems solved independently in three languages:

- `com/dev/` — Java (package root `com.dev`)
- `python/` — Python
- `typescript/` — TypeScript

Each language directory has its own parallel set of category folders
(easy/learn problems, `leetcodehard`, `systemdesign`) and its own tiny logging
helper that every solution file imports instead of using raw
print/`console.log`. There is no cross-language shared code and no single
build that spans all three — treat each language directory as its own
self-contained workspace.

Every solution file is self-contained: a short header comment (LeetCode URL +
`status` for algorithm problems, or a one-line description for system-design
problems), the implementation, and inline test cases executed via that
language's own entrypoint (`main`, `__main__`, or bottom-of-file script code).
There is no separate test runner or test directory in this repo — correctness
is verified by each file's own printed expected-vs-actual output when you run
it directly.

## Java (`com/dev/`)

No build tool (no Maven/Gradle) — compile and run files directly with
`javac`/`java`, using the repo root as the classpath so package imports
resolve:

```bash
# compile everything in a category (plus the logger it depends on)
javac -d /tmp/out -cp . com/dev/logger/basePrinter.java com/dev/leetcodehard/*.java

# run one solution
java -cp /tmp/out com.dev.leetcodehard.MedianOfTwoSortedArrays
```

Convention: every class `extends com.dev.logger.basePrinter` and calls the
inherited static `logp(String)` for output rather than `System.out.println`
directly.

## Python (`python/`)

No package manager beyond `requirements.txt` (just `ipython`, `flake8`).
Scripts import the shared logger as `from base_logger.logging_event import
create_logger`, which only resolves if `python/` itself is on the path:

```bash
# run one solution from the repo root
PYTHONPATH=python python3 python/leetcodehard/median_of_two_sorted_arrays.py

# lint (config lives in python/setup.cfg: max-line-length=100, max-complexity=10)
python3 -m flake8 --max-line-length=100 python/
```

Convention: each file defines a `Solution` class (algorithm problems) or a
plain class (system-design problems) with a class-level
`log = create_logger(__name__)`, and a `if __name__ == "__main__":` block that
exercises it and logs the result.

## TypeScript (`typescript/`)

Has its own `package.json`/`tsconfig.json` — install deps once, then use
`ts-node` directly (no build step, no bundler):

```bash
cd typescript
npm install
npx tsc --noEmit -p tsconfig.json         # type-check everything
npx ts-node leetcodehard/medianOfTwoSortedArrays.ts   # run one file
```

`tsconfig.json` is strict mode, target ES2022, commonjs modules. Convention:
files import `logp` from `../utils/logger` for output; filenames are
camelCase versions of the algorithm/component name.
