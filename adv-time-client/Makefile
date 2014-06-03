
PROF = dev
#PROF = prod
#PROF = dev,test,srcmap
# PROF = prod,test
# PROF = prod

CLJSBUILD = client

all: autocompile

run: autocompile

openbrowser:
	(sleep 1 && open index.html) &

autocompile:
	rm -rf target
	lein with-profile $(PROF) cljsbuild auto $(CLJSBUILD)

prod:
	rm -rf target
	lein with-profile prod cljsbuild once $(CLJSBUILD)

clean:
	lein -o clean
