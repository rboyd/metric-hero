# treemapper

Given a directory, outputs a JSON file in the format expected by the [d3
Treemap Layout](https://github.com/mbostock/d3/wiki/Treemap-Layout)

## Example

A treemap of the [ClojureScript
One](https://github.com/brentonashworth/one) codebase:

![ClojureScript One Treemap](https://raw.github.com/wiki/rboyd/treemapper/images/one-map.png)

## Usage

(spit "somedir.json (treemap.core/walk "/somedir"))




## License

Copyright © 2013 Robert Boyd

Distributed under the Eclipse Public License, the same as Clojure.
