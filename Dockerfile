FROM curlimages/curl:8.1.1 as curl-base

FROM fpr-backend-temp

COPY --chown=cnb --from=curl-base /usr/bin/curl /usr/bin/curl
COPY --chown=cnb --from=curl-base /usr/lib/libcurl.so.4 /usr/lib/libcurl.so.4
COPY --chown=cnb --from=curl-base /lib/ld-musl-x86_64.so.1 /lib/ld-musl-x86_64.so.1

COPY --chown=cnb --from=curl-base /lib/libz.so.1 /lib/libz.so.1
COPY --chown=cnb --from=curl-base /usr/lib/libnghttp2.so.14 /usr/lib/libnghttp2.so.14
COPY --chown=cnb --from=curl-base /usr/lib/libssh2.so.1 /usr/lib/libssh2.so.1

COPY --chown=cnb --from=curl-base /lib/libssl.so.3 /lib/libssl.so.3
COPY --chown=cnb --from=curl-base /lib/libcrypto.so.3 /lib/libcrypto.so.3
COPY --chown=cnb --from=curl-base /usr/lib/libbrotlidec.so.1 /usr/lib/libbrotlidec.so.1
COPY --chown=cnb --from=curl-base /usr/lib/libbrotlicommon.so.1 /usr/lib/libbrotlicommon.so.1