#!/usr/bin/env -S deno run
import * as esbuild from "npm:esbuild-wasm";
await esbuild.build<esbuild.BuildOptions>({
  entryPoints: ["stomp-test.mts"],
  allowOverwrite: true,
  outfile: "stomp-test.mjs",
});
