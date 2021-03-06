# refunc

React stateless functional components using ClojureScript.

Minimal adapter, includes Hiccup style markup generation,
`refunc.core/html` and render loop, `refunc.core/render!`.

See [`examples/todomvc/src/todomvc/core.cljs`](https://github.com/hraberg/refunc/blob/master/examples/todomvc/src/todomvc/core.cljs).

## Setup

To get an interactive development environment run:

    lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).
This will auto compile and send all changes to the browser without the
need to reload. After the compilation process is complete, you will
get a Browser Connected REPL. An easy way to try it is:

    (js/alert "Am I connected?")

and you should see an alert in the browser window.

To clean all compiled files:

    lein clean

To create a production build run:

    lein cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL.

## References

* https://facebook.github.io/react/blog/2015/10/07/react-v0.14.html#stateless-functional-components
* https://github.com/hraberg/domaren

## License

Copyright © 2015 Håkan Råberg

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
