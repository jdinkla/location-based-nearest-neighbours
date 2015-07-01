# (c) 2015 Jörn Dinkla, www.dinkla.com 
#
# R code for some diagrams for the lbnn app
# 
#
# $ install.packages(c("ggplot2"))
# 
# for pretty fonts the following extra package is needed
#
# $ install.packages("extrafont")
#
# you have to import all the windows fonts with
#
# $ library(extrafont)
# $ font_import()
#
# but warning: this may take a while 
# see http://blog.revolutionanalytics.com/2012/09/how-to-use-your-favorite-fonts-in-r-charts.html
# and http://www.r-bloggers.com/change-fonts-in-ggplot2-and-create-xkcd-style-graphs/
#

setwd("C:/workspace/location-based-nearest-neighbours/temp")

library(ggplot2)
library(extrafont)

lbnn_init <- function() {
#font_import()
	loadfonts()
#	loadfonts(device="win")
}

use_hdfs <- FALSE
	# if (use_hdfs) {
	# 	rs <- NULL
	# } else {
	# 	rs <- read.csv(file=filename, header=TRUE, sep=";")
	# }
	# rs


#
# Definitions
#

dinkla_blue <- "#1E2D5B"
dinkla_dark_blue <- "#0F162E"
dinkla_red <- "#AF0A14"
font_family <- "Verdana"

#
# auxiliary functions 
#

# convert a string to a date
# see http://stackoverflow.com/questions/16402064/problems-formatting-date-into-format-y-m
yyyymm_as_date <- function(xs) {
	as.Date(paste(xs, "01", sep=""), format="%Y%m%d")
}

# convert a string to a date
yyyymmdd_as_date <- function(xs) {
	as.Date(xs, format="%Y%m%d")
}

# read a csv file with a text key and numeric values
read_textkey_value <- function(file, n=1) {
	read.csv(file=file, header=TRUE, sep=";", colClasses=c("character",rep("numeric", n)))
}

# create a color from 'color'
mk_color <- function(color,alpha) {
	v <- col2rgb(color)
	rgb(v[1]/255, v[2]/255, v[3]/255, alpha)
}

# create a palette from 'color'
mk_palette <- function(color, n = 10, alpha_start = 0.1, alpha_end = 0.9) {
	v <- col2rgb(color)
	w <- v/255.0
	alpha_width <- alpha_end - alpha_start
	alpha_delta <- alpha_width / n
	alphas <- seq(alpha_start, alpha_end, alpha_delta)
	f <- function(alpha) {
		rgb(w[1], w[2], w[3], alpha)
	}
	mapply(f, alphas)
}

#
# sums_hh: checkins per hour 
#
# displayed in a pie chart
#

sums_hh <- read_textkey_value(file="sums_hh.csv")

chart_hh <- function() {
	vs <- sums_hh$value
	cs <- mk_palette(dinkla_blue, length(vs), 0.5, 0.95)
	ls <- sums_hh$hh
	pct <- round(vs/sum(vs)*100)
	ls <- paste(paste(ls, pct, sep="\n"), "%", sep="")
	pie(vs, col=cs, labels=ls, clockwise=TRUE, border=NULL, main="Number of checkins during the day", family="Verdana", col.main=dinkla_red, col.lab=dinkla_dark_blue, cex=1.34, cex.main=3)
}

#
# sums_ym: checkins per month
#
# displayed in a bar chart
#

sums_ym <- read_textkey_value(file="sums_yyyymm.csv")
sums_ym$yyyymm <- yyyymm_as_date(sums_ym$yyyymm)

chart_ym <- function(scale = 1) {

	size_small <- 12 * scale
	size_middle <- 16 * scale
	size_large <- 24 * scale

	p <- ggplot(sums_ym, aes(x=yyyymm, y=value)) + 
			geom_bar(stat="identity", fill=dinkla_blue, color=dinkla_blue) + 
			theme(
				panel.background = element_rect(fill=mk_color(dinkla_blue, 0.1)),
				panel.grid.major.x = element_blank(),
    		    panel.grid.minor.x = element_blank()) + 
			ggtitle("Number of check-ins per month in the loc-gowalla dataset") +
   			xlab("Month") + ylab("Number of check-ins")  +
			theme(
			axis.title.x=element_text(size=size_middle, family=font_family, lineheight=.9, colour=dinkla_red),
			axis.text.x=element_text(size=size_small, color=dinkla_blue, family=font_family),
			axis.title.y=element_text(size=size_middle, family=font_family, lineheight=.9, colour=dinkla_red),
			axis.text.y=element_text(size=size_small, color=dinkla_blue, family=font_family),
			plot.title=element_text(size=size_large, color=dinkla_red, family=font_family)
			)
   	p
}

#
# sums_ymd: checkins per day
#
# displayed in a line graph
#

sums_ymd <- read_textkey_value(file="sums_yyyymmdd.csv")
sums_ymd$yyyymmdd <- yyyymmdd_as_date(sums_ymd$yyyymmdd)

chart_ymd <- function(scale = 1) {

	size_small <- 12 * scale
	size_middle <- 16 * scale
	size_large <- 24 * scale

	a <- sums_ymd
	#a$smoothed <- as.vector(smooth(a$value, ))
	a$smoothed <- filter(a$value, rep(1/7, 7), sides=2)
	p <- ggplot(a, aes(x=yyyymmdd, y=value)) + 
			geom_area(fill=dinkla_blue, alpha=.3) + 
			geom_line(color=dinkla_blue) + 
			geom_line(aes(y=smoothed), color=dinkla_red) + 
			theme(
				panel.background = element_rect(fill=mk_color(dinkla_blue, 0.1)),
				legend.position=c(0.1, 0.7)) + 
			ggtitle("Number of check-ins per day in the loc-gowalla dataset") +
   			xlab("Date") + ylab("Number of check-ins") +
			theme(
				axis.title.x=element_text(size=size_middle, family=font_family, lineheight=.9, colour=dinkla_red),
				axis.text.x=element_text(size=size_small, color=dinkla_blue, family=font_family),
				axis.title.y=element_text(size=size_middle, family=font_family, lineheight=.9, colour=dinkla_red),
				axis.text.y=element_text(size=size_small, color=dinkla_blue, family=font_family),
				plot.title=element_text(size=size_large, color=dinkla_red, family=font_family)
			) 

	p
}

#
#
#

sums_loc <- read.csv(file="sums_location.csv", header=TRUE, sep=";")
sums_ns1 <- read.csv(file="num_neighbors_20091006_5.0.csv", header=TRUE, sep=";")

# define the charts



hide <- function() {

qplot(sums_ym$yyyymm, sums_ym$value, geom="line")
qplot(sums_ym$yyyymm, sums_ym$value, geom=c("line", "point"))
qplot(sums_ym$yyyymm, sums_ym$value, geom="bar", stat="identity")
ggplot(sums_ym, aes(x=yyyymm, y=value)) + geom_bar(stat="identity", fill=dinkla_blue, color="black")

qplot(sums_ymd$yyyymm, sums_ymd$value, geom="line")
qplot(sums_ymd$yyyymm, sums_ymd$value, geom=c("line", "point"))
ggplot(sums_ymd, aes(x=yyyymmdd, y=value)) + geom_line(color=dinkla_blue)

hist(sums_ns1$number.of.neighbors)
hist(sums_ns1$number.of.neighbors, breaks=25)
qplot(sums_ns1$number.of.neighbors, binwidth=2)

b <- sums_ymd
b$type <- rep("Check-ins", length(sums_ymd$value))
c <- sums_ymd
c$value <- smooth(sums_ymd$value)
c$type <- rep("Smoothed", length(c$value))
d <- rbind(b, c)
ggplot(d, aes(x=yyyymmdd, y=value, color=type)) + geom_line() + scale_color_manual(values=c(dinkla_blue, dinkla_red))
ggplot(sums_ymd, aes(x=yyyymmdd, y=value)) + geom_area(fill=dinkla_blue, alpha=.3) + geom_line(color=dinkla_blue)


# sums_ym
ggplot(sums_ym, aes(x=yyyymm, y=value)) + geom_bar(stat="identity", fill=dinkla_blue)



#ggsave("font_ggplot.pdf", plot=p,  width=16, height=9)
## needed for Windows - make sure YOU have the correct path for your machine:
#Sys.setenv(R_GSCMD = "C:\\Program Files (x86)\\gs\\gs9.06\\bin\\gswin32c.exe")
#embed_fonts("font_ggplot.pdf")

}



# mk_blues <- function(n = 10) {
# 	mk_palette(dinkla_blue, n, 0.05, 0.95)
# }

# # 
# gen_chart <- function(ls, vs, mn="Main") {
# 	cs <- rainbow(length(vs))
# 	barplot(vs, col=cs, main=mn)
# }

# chart_y <- function() {
# 	ls <- sums_y$yyyy
# 	vs <- sums_y$value
# 	cs <- rainbow(length(ls))
# 	barplot(vs, col=cs, main="Number of check-in's per year", names.arg=ls)
# }

