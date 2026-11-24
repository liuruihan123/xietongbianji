import { listQaCase } from './../../system/qaCase/index';
import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { KnowledgeAttachVO, KnowledgeAttachForm, KnowledgeAttachQuery } from '@/api/knowledge/knowledgeAttach/types';

/**
 * 查询知识库文件列表
 * @param query
 * @returns {*}
 */

export const listKnowledgeAttach = (query?: KnowledgeAttachQuery): AxiosPromise<KnowledgeAttachVO[]> => {
  return request({
    url: '/knowledge/knowledgeAttach/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询知识库文件详细
 * @param id
 */
export const getKnowledgeAttach = (id: string | number): AxiosPromise<KnowledgeAttachVO> => {
  return request({
    url: '/knowledge/knowledgeAttach/' + id,
    method: 'get'
  });
};

/**
 * 新增知识库文件
 * @param data
 */
export const addKnowledgeAttach = (data: KnowledgeAttachForm) => {
  return request({
    url: '/knowledge/knowledgeAttach',
    method: 'post',
    data: data
  });
};

/**
 * 修改知识库文件
 * @param data
 */
export const updateKnowledgeAttach = (data: KnowledgeAttachForm) => {
  return request({
    url: '/knowledge/knowledgeAttach',
    method: 'put',
    data: data
  });
};

/**
 * 删除知识库文件
 * @param id
 */
export const delKnowledgeAttach = (id: string | number | Array<string | number>) => {
  return request({
    url: '/knowledge/knowledgeAttach/' + id,
    method: 'delete'
  });
};
