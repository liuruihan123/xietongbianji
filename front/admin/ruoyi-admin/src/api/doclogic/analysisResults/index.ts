import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { AnalysisResultsVO, AnalysisResultsForm, AnalysisResultsQuery } from '@/api/doclogic/analysisResults/types';

/**
 * 查询 分析结果列表
 * @param query
 * @returns {*}
 */

export const listAnalysisResults = (query?: AnalysisResultsQuery): AxiosPromise<AnalysisResultsVO[]> => {
  return request({
    url: '/doclogic/analysisResults/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询 分析结果详细
 * @param id
 */
export const getAnalysisResults = (id: string | number): AxiosPromise<AnalysisResultsVO> => {
  return request({
    url: '/doclogic/analysisResults/' + id,
    method: 'get'
  });
};

/**
 * 新增 分析结果
 * @param data
 */
export const addAnalysisResults = (data: AnalysisResultsForm) => {
  return request({
    url: '/doclogic/analysisResults',
    method: 'post',
    data: data
  });
};

/**
 * 修改 分析结果
 * @param data
 */
export const updateAnalysisResults = (data: AnalysisResultsForm) => {
  return request({
    url: '/doclogic/analysisResults',
    method: 'put',
    data: data
  });
};

/**
 * 删除 分析结果
 * @param id
 */
export const delAnalysisResults = (id: string | number | Array<string | number>) => {
  return request({
    url: '/doclogic/analysisResults/' + id,
    method: 'delete'
  });
};
