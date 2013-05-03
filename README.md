# Metric Hero

> The hero is one who kindles a great light in the world, who sets up blazing torches in the dark streets of life for men to see by.

> -- *Felix Adler*

Metric Hero analyzes your codebase and builds interactive visualizations. Use it to more effectively learn your way around a new codebase, or to focus your efforts on evolving existing projects.


## Example

A Metric Hero render of the [Ruby on Rails](https://github.com/rails/rails) codebase:

![Rails Render](https://raw.github.com/wiki/rboyd/metric-hero/images/rails.png)


## About

This technique of software visualization is largely based on work from
[Richard Wettel](http://www.inf.usi.ch/phd/wettel/index.html) and
[Michele Lanza](http://www.inf.usi.ch/faculty/lanza/) -- specifically
Wettel's Ph.D. thesis
[Software Systems as Cities](http://www.inf.usi.ch/phd/wettel/download.php?f=Wettel10b-PhDThesis.pdf).

In the current incarnation, a git repository is analyzed and
projected onto three dimensions. Each building of the city represents
a file on disk. The width and length of the buildings
in the city are representative of the file size. The buildings are
laid out using a
[Treemap tiling algorithm](http://en.wikipedia.org/wiki/Treemapping#The_tiling_algorithm).
The height of the buildings are determined based on relative commit
times. Files that have been updated recently are taller.


## Usage

```clojure
(require 'metric-hero.core)
(metric-hero.core/analyze "/path/to/repo" :ignore [#"ignored" #"regex"])
```

* Copy output.json into resources/public
* Compile ClojureScript ```lein cljsbuild once```
* Start jetty: ```lein run```
* Visit ```http://localhost:3000/```
* Navigate with WASD, QE, RF, and Arrow Keys


## License

Copyright © 2013 Robert Boyd

Distributed under the Eclipse Public License, the same as Clojure.
