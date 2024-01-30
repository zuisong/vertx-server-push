#!/usr/bin/env -S deno run -A
import * as esbuild from "npm:esbuild";
await esbuild.build<esbuild.BuildOptions>({
  entryPoints: ["stomp-test.mts"],
  allowOverwrite: true,
  outfile: "stomp-test.mjs",
});
